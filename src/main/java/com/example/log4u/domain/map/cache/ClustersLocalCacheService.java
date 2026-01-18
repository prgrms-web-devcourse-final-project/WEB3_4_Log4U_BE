package com.example.log4u.domain.map.cache;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.map.cache.manager.ClustersCacheManager;
import com.example.log4u.domain.map.cache.manager.ClustersLocalCacheManager;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClustersLocalCacheService {

	private final ClustersCacheManager clustersCacheManager;
	private final ClustersLocalCacheManager clustersLocalCacheManager;

	private final RetryExecutor retryExecutor;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void refreshOnSchedule() {
		clustersLocalCacheManager.evictAll();
	}

	public void refresh(String geohash, int level) {
		clustersCacheManager.refresh(geohash, level);
		clustersLocalCacheManager.evict(geohash, level);
	}

	public List<GetDiaryClusterResponse> getClusters(String geohash, int level) {
		return retryExecutor.runWithRetry(() -> {
			List<GetDiaryClusterResponse> clusters = loadWithFallback(geohash,level);
			return clusters;
		});
	}

	private List<GetDiaryClusterResponse> loadWithFallback(String geohash, int level) {
		List<GetDiaryClusterResponse> clusters = clustersLocalCacheManager.load(geohash,level);
		if (clusters != null) {
			return clusters;
		}

		clusters = clustersCacheManager.load(geohash, level);
		if (clusters == null) {
			clusters = clustersCacheManager.loadAndCache(geohash, level);
		}
		clustersLocalCacheManager.cache(geohash, level, clusters);
		return clusters;
	}
}
