package com.example.log4u.domain.map.cache.manager;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.common.infra.local_cache.LocalCacheManager;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClustersLocalCacheManager {

	private static final String CLUSTERS_CACHE_NAME = "clusters";
	private static final String CLUSTERS_CACHE_KEY = "cluster:geohash:%s:level:%d";

	private final LocalCacheManager localCacheManager;

	private final Cache<String, List<GetDiaryClusterResponse>> clustersLocalCache = Caffeine
		.newBuilder()
		.recordStats()
		.expireAfterWrite(Duration.ofMinutes(10))
		.maximumSize(5)
		.build();

	@PostConstruct
	public void registerCache() {
		localCacheManager.addLocalCache(CLUSTERS_CACHE_NAME, clustersLocalCache);
	}

	public void evict(String geohash, int level) {
		String key = CLUSTERS_CACHE_KEY.formatted(geohash, level);
		localCacheManager.publishInvalidateCacheMessage(CLUSTERS_CACHE_NAME, key);
	}

	public void cache(String geohash, int level, List<GetDiaryClusterResponse> clusters) {
		String key = CLUSTERS_CACHE_KEY.formatted(geohash, level);
		clustersLocalCache.put(key, clusters);
	}

	public List<GetDiaryClusterResponse> load(String geohash, int level) {
		String key = CLUSTERS_CACHE_KEY.formatted(geohash, level);
		return clustersLocalCache.getIfPresent(key);
	}

	public void evictAll() {
		clustersLocalCache.invalidateAll();
	}
}
