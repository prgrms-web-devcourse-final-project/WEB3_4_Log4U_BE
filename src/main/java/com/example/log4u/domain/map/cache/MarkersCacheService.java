package com.example.log4u.domain.map.cache;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.cache.manager.MarkersCacheManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkersCacheService {

	private final MarkersCacheManager markersCacheManager;

	private final RetryExecutor retryExecutor;

	public void refresh(String geohash) {
		markersCacheManager.refresh(geohash);
	}

	public List<Diary> getMarkers(String geohash) {
		return retryExecutor.runWithRetry(() -> {
			List<Diary> markers = markersCacheManager.load(geohash);
			if (markers == null) {
				markers = markersCacheManager.loadAndCache(geohash);
			}
			return markers;
		});
	}

	public List<Diary> getTopLikedMarkers(String geohash) {
		return retryExecutor.runWithRetry(() -> {
			List<Diary> markers = markersCacheManager.load(geohash);
			if (markers == null) {
				markers = markersCacheManager.loadAndCache(geohash);
			}
			return DiaryUtils.topByLikes(markers);
		});
	}
}
