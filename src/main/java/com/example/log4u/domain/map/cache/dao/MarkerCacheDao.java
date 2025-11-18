package com.example.log4u.domain.map.cache.dao;

import static com.example.log4u.common.config.redis.ObjectMapperFactory.*;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.common.infra.cache.CacheManager;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryGeoHashRepository;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.cache.CacheKeyGenerator;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkerCacheDao {

	private final CacheManager cacheManager;

	private final DiaryRepository diaryRepository;
	private final DiaryGeoHashRepository diaryGeoHashRepository;

	public List<DiaryMarkerResponseDto> load(String geohash) {
		String value = cacheManager.get(CacheKeyGenerator.markerCacheKey(geohash));
		if (value == null) {
			return null;
		}
		return convertToMarkers(value);
	}

	private List<DiaryMarkerResponseDto> convertToMarkers(String value) {
		return readValue(value, new TypeReference<>() {
		});
	}

	public List<DiaryMarkerResponseDto> loadAndCache(String geohash) {
		List<DiaryMarkerResponseDto> markers = loadMarkersFromDb(geohash);
		cache(markers, geohash);
		return markers;
	}

	private List<DiaryMarkerResponseDto> loadMarkersFromDb(String geohash) {
		List<Long> diaryIds = diaryGeoHashRepository.findDiaryIdByGeohash(geohash);
		if (diaryIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<Diary> diaries = diaryRepository.findAllById(diaryIds);
		return diaries.stream()
			.map(DiaryMarkerResponseDto::of)
			.toList();
	}

	private void cache(List<DiaryMarkerResponseDto> markers, String geohash) {
		String key = CacheKeyGenerator.markerCacheKey(geohash);
		cacheManager.cache(key, writeValueAsString(markers), RedisTTLPolicy.MARKER_TTL);
	}

	public void evict(String geohash) {
		cacheManager.evict(CacheKeyGenerator.markerCacheKey(geohash));
	}
}
