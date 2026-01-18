package com.example.log4u.domain.map.cache;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.cache.manager.MarkersCacheManager;
import com.example.log4u.domain.map.cache.manager.MarkersLocalCacheManager;
import com.example.log4u.domain.map.cache.support.DiaryUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkersLocalCacheService {

	private final MarkersCacheManager markersCacheManager;
	private final MarkersLocalCacheManager markersLocalCacheManager;

	private final RetryExecutor retryExecutor;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void refreshOnSchedule() {
		markersLocalCacheManager.evictAll();
	}

	public void refresh(String geohash) {
		markersCacheManager.refresh(geohash);
		markersLocalCacheManager.evict(geohash);
	}

	public List<Diary> getTopLikedMarkers(String geohash) {
		return retryExecutor.runWithRetry(() -> {
			List<Diary> markers = loadWithFallback(geohash);
			return DiaryUtils.topByLikes(markers);
		});
	}

	private List<Diary> loadWithFallback(String geohash) {
		List<Diary> markers = markersLocalCacheManager.load(geohash);
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
