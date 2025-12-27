package com.example.log4u.domain.map.cache;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.map.cache.manager.MarkersCacheManager;
import com.example.log4u.domain.map.cache.manager.MarkersLocalCacheManager;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkersLocalCacheService {

	private final MarkersCacheManager markersCacheManager;
	private final MarkersLocalCacheManager markersLocalCacheManager;

	private final RetryExecutor retryExecutor;

	public void refresh(String geohash) {
		markersCacheManager.refresh(geohash);
		markersLocalCacheManager.evict(geohash);
	}

	public List<GetDiaryMarkerResponse> getMarkers(String geohash) {
		return retryExecutor.runWithRetry(() -> {
			List<GetDiaryMarkerResponse> markers = loadWithFallback(geohash);
			return markers;
		});
	}

	private List<GetDiaryMarkerResponse> loadWithFallback(String geohash) {
		List<GetDiaryMarkerResponse> markers = markersLocalCacheManager.load(geohash);
		if (markers != null) {
			return markers;
		}

		markers = markersCacheManager.load(geohash);
		if (markers == null) {
			markers = markersCacheManager.loadAndCache(geohash);
		}
		markersLocalCacheManager.cache(geohash, markers);
		return markers;
	}
}
