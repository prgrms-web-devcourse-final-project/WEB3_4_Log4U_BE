package com.example.log4u.domain.map.cache.dao;

import static com.example.log4u.common.config.redis.ObjectMapperFactory.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.common.executor.DistributedLockExecutor;
import com.example.log4u.common.infra.cache.CacheManager;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.exception.InvalidMapLevelException;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClusterCacheDao {

	private static final String CLUSTER_CACHE_KEY = "cluster:geohash:%s:level:%d";
	public static final String CLUSTER_LOCK_KEY = "cluster-lock:%s";

	private final CacheManager cacheManager;

	private final DistributedLockExecutor distributedLockExecutor;

	private final SidoAreasRepository sidoAreasRepository;
	private final SiggAreasRepository siggAreasRepository;

	public List<DiaryClusterResponseDto> load(String geohash, int level) {
		String key = String.format(CLUSTER_CACHE_KEY, geohash, level);
		String value = cacheManager.get(key);
		if (value == null) {
			return null;
		}
		return convertToClusters(value);
	}

	private List<DiaryClusterResponseDto> convertToClusters(String value) {
		return readValue(value, new TypeReference<>() {
		});
	}

	public List<DiaryClusterResponseDto> loadAndCache(String geohash, int level) {
		return distributedLockExecutor.runWithLock(CLUSTER_LOCK_KEY.formatted(geohash), () -> {
			List<DiaryClusterResponseDto> clusters = loadClustersFromDb(geohash, level);
			cache(clusters, geohash, level);
			return clusters;
		});
	}

	private List<DiaryClusterResponseDto> loadClustersFromDb(String geohash, int level) {
		return switch (level) {
			case 1 -> sidoAreasRepository.findByGeohashPrefix(geohash);
			case 2 -> siggAreasRepository.findByGeohashPrefix(geohash);
			default -> throw new InvalidMapLevelException();
		};
	}

	private void cache(List<DiaryClusterResponseDto> clusters, String geohash, int level) {
		String key = String.format(CLUSTER_CACHE_KEY, geohash, level);
		cacheManager.cache(key, writeValueAsString(clusters), RedisTTLPolicy.CLUSTER_TTL);
	}

	public void evictSido(String geohash) {
		String key = String.format(CLUSTER_CACHE_KEY, geohash, 1);
		cacheManager.evict(key);
	}

	public void evictSigg(String geohash) {
		String key = String.format(CLUSTER_CACHE_KEY, geohash, 2);
		cacheManager.evict(key);
	}

}
