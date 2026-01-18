package com.example.log4u.domain.map.cache.manager;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.common.infra.local_cache.LocalCacheManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarkersLocalCacheManager {

	private static final String MARKERS_CACHE_NAME = "markers";
	private static final String MARKERS_CACHE_KEY = "marker:geohash:%s";

	private final LocalCacheManager localCacheManager;

	private final Cache<String, List<Diary>> markersLocalCache = Caffeine
		.newBuilder()
		.recordStats()
		.expireAfterWrite(Duration.ofMinutes(10))
		.maximumSize(5)
		.build();

	@PostConstruct
	public void registerCache() {
		localCacheManager.addLocalCache(MARKERS_CACHE_NAME, markersLocalCache);
	}

	public void evict(String geohash) {
		String key = MARKERS_CACHE_KEY.formatted(geohash);
		localCacheManager.publishInvalidateCacheMessage(MARKERS_CACHE_NAME, key);
	}

	public void cache(String geohash, List<Diary> markers) {
		String key = MARKERS_CACHE_KEY.formatted(geohash);
		markersLocalCache.put(key, markers);
	}

	public List<Diary> load(String geohash) {
		String key = MARKERS_CACHE_KEY.formatted(geohash);
		return markersLocalCache.getIfPresent(key);
	}

	public void evictAll() {
		markersLocalCache.invalidateAll();
	}
}
