package com.example.log4u.domain.map.cache;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.map.cache.manager.MarkersCacheManager;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkersCacheService {

	private final MarkersCacheManager markersCacheManager;

	private final RetryExecutor retryExecutor;

	public void refresh(String geohash) {
		markersCacheManager.refresh(geohash);
	}

	public List<GetDiaryMarkerResponse> getMarkers(String geohash) {
		return retryExecutor.runWithRetry(() -> {
			List<GetDiaryMarkerResponse> markers = markersCacheManager.load(geohash);
			if (markers == null) {
				markers = markersCacheManager.loadAndCache(geohash);
			}
			return markers;
		});
	}
}
