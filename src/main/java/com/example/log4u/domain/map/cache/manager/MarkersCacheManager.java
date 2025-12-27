package com.example.log4u.domain.map.cache.manager;

import static com.example.log4u.common.config.redis.ObjectMapperFactory.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.common.executor.DistributedLockExecutor;
import com.example.log4u.common.infra.cache.CacheManager;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkersCacheManager {

	private static final String MARKER_CACHE_KEY = "marker:geohash:%s";
	private static final String MARKER_LOCK_KEY = "marker-lock:%s";

	private final CacheManager cacheManager;

	private final DistributedLockExecutor distributedLockExecutor;

	private final DiaryRepository diaryRepository;

	public void refresh(String geohash) {
		String lockKey = MARKER_LOCK_KEY.formatted(geohash);
		String cacheKey = MARKER_CACHE_KEY.formatted(geohash);

		distributedLockExecutor.runWithLock(lockKey, () -> {
			cacheManager.evict(cacheKey);
			List<GetDiaryMarkerResponse> markers = loadMarkersFromDb(geohash);
			cache(markers, geohash);
		});
	}

	public List<GetDiaryMarkerResponse> load(String geohash) {
		String key = MARKER_CACHE_KEY.formatted(geohash);
		String value = cacheManager.get(key);
		if (value == null) {
			return null;
		}
		return convertToMarkers(value);
	}

	private List<GetDiaryMarkerResponse> convertToMarkers(String value) {
		return readValue(value, new TypeReference<>() {
		});
	}

	public List<GetDiaryMarkerResponse> loadAndCache(String geohash) {
		String lockKey = MARKER_LOCK_KEY.formatted(geohash);

		return distributedLockExecutor.runWithLock(lockKey, () -> {
				List<GetDiaryMarkerResponse> markers = loadMarkersFromDb(geohash);
				cache(markers, geohash);
				return markers;
			});
	}

	private List<GetDiaryMarkerResponse> loadMarkersFromDb(String geohash) {
		List<Diary> diaries = diaryRepository.findDiariesByGeohash(geohash);
		return diaries.stream()
			.map(GetDiaryMarkerResponse::of)
			.toList();
	}

	private void cache(List<GetDiaryMarkerResponse> markers, String geohash) {
		String key = MARKER_CACHE_KEY.formatted(geohash);
		cacheManager.cache(key, writeValueAsString(markers), RedisTTLPolicy.MARKER_TTL);
	}

	public void evict(String geohash) {
		String key = MARKER_CACHE_KEY.formatted(geohash);
		cacheManager.evict(key);
	}
}
