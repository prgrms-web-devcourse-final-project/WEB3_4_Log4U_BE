package com.example.log4u.domain.map.cache;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.map.cache.manager.ClustersCacheManager;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClustersCacheService {

	private final ClustersCacheManager clustersCacheManager;

	private final RetryExecutor retryExecutor;

	public void refresh(String geohash, int level) {
		clustersCacheManager.refresh(geohash, level);
	}

	public List<GetDiaryClusterResponse> getClusters(String geohash, int level) {
		return retryExecutor.runWithRetry(() -> {
			List<GetDiaryClusterResponse> clusters = clustersCacheManager.load(geohash, level);
			if (clusters == null) {
				clusters = clustersCacheManager.loadAndCache(geohash, level);
			}
			return clusters;
		});
	}
}
