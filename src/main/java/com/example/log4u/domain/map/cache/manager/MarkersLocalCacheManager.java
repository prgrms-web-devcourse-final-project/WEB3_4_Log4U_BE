package com.example.log4u.domain.map.cache.manager;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MarkersLocalCacheManager {

	private static final String MARKERS_CACHE_KEY = "marker:geohash:%s";

	private final Cache<String, List<Diary>> markersLocalCache = Caffeine
		.newBuilder()
		.recordStats()
		.expireAfterWrite(Duration.ofMinutes(10))
		.maximumSize(1)
		.build();

	public void cache(String geohash, List<Diary> markers) {
		markersLocalCache.put(MARKERS_CACHE_KEY.formatted(geohash), markers);
	}

	public List<Diary> load(String geohash) {
		return markersLocalCache.getIfPresent(MARKERS_CACHE_KEY.formatted(geohash));
	}

	public void evict(String geohash) {
		markersLocalCache.invalidate(MARKERS_CACHE_KEY.formatted(geohash));
	}
}
