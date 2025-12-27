package com.example.log4u.domain.map.cache.manager;

import static com.example.log4u.common.config.redis.ObjectMapperFactory.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.common.executor.DistributedLockExecutor;
import com.example.log4u.common.infra.cache.CacheManager;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.example.log4u.domain.map.exception.InvalidMapLevelException;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClustersCacheManager {

	private static final String CLUSTER_CACHE_KEY = "cluster:geohash:%s:level:%d";
	private static final String CLUSTER_LOCK_KEY  = "cluster-lock:%s:level:%d";

	private final CacheManager cacheManager;
	private final DistributedLockExecutor distributedLockExecutor;

	private final SidoAreasRepository sidoAreasRepository;
	private final SiggAreasRepository siggAreasRepository;

	public void refresh(String geohash, int level) {
		String lockKey  = CLUSTER_LOCK_KEY.formatted(geohash, level);
		String cacheKey = CLUSTER_CACHE_KEY.formatted(geohash, level);

		distributedLockExecutor.runWithLock(lockKey, () -> {
			cacheManager.evict(cacheKey);
			List<GetDiaryClusterResponse> clusters = loadClustersFromDb(geohash, level);
			cache(clusters, geohash, level);
		});
	}

	public List<GetDiaryClusterResponse> load(String geohash, int level) {
		String key = CLUSTER_CACHE_KEY.formatted(geohash, level);
		String value = cacheManager.get(key);
		if (value == null) {
			return null;
		}
		return convertToClusters(value);
	}

	private List<GetDiaryClusterResponse> convertToClusters(String value) {
		return readValue(value, new TypeReference<>() {});
	}

	public List<GetDiaryClusterResponse> loadAndCache(String geohash, int level) {
		String lockKey = CLUSTER_LOCK_KEY.formatted(geohash, level);

		return distributedLockExecutor.runWithLock(lockKey, () -> {
			List<GetDiaryClusterResponse> clusters = loadClustersFromDb(geohash, level);
			cache(clusters, geohash, level);
			return clusters;
		});
	}

	private List<GetDiaryClusterResponse> loadClustersFromDb(String geohash, int level) {
		return switch (level) {
			case 1 -> sidoAreasRepository.findSidoAreasCluster(geohash);
			case 2 -> siggAreasRepository.findSiggAreasCluster(geohash);
			default -> throw new InvalidMapLevelException();
		};
	}

	private void cache(List<GetDiaryClusterResponse> clusters, String geohash, int level) {
		String key = CLUSTER_CACHE_KEY.formatted(geohash, level);
		cacheManager.cache(key, writeValueAsString(clusters), RedisTTLPolicy.CLUSTER_TTL);
	}
}
