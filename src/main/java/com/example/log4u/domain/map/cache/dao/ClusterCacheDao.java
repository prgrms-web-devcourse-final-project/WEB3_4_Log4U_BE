package com.example.log4u.domain.map.cache.dao;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.cache.CacheKeyGenerator;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.exception.InvalidMapLevelException;
import com.example.log4u.domain.map.repository.sido.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;


@Component
@RequiredArgsConstructor
@Slf4j
public class ClusterCacheDao {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private final SidoAreasRepository sidoAreasRepository;
	private final SiggAreasRepository siggAreasRepository;

	public List<DiaryClusterResponseDto> load(String geohash, int level) {
		String key = CacheKeyGenerator.clusterCacheKey(geohash, level);
		try {
			String value = redisTemplate.opsForValue().get(key);
			if (value == null){
				return null;
			}
			return objectMapper.readValue(value, new TypeReference<>() {
				});
		} catch (Exception e) {
			log.warn("[REDIS][CLUSTER][GET][ERR] key={}", key, e);
			throw new RuntimeException(e);
		}
	}

	public List<DiaryClusterResponseDto> loadAndCache(String geohash, int level) {
		List<DiaryClusterResponseDto> clusters = loadClustersFromDb(geohash, level);
		cache(clusters,geohash, level);
		return clusters;
	}

	private List<DiaryClusterResponseDto> loadClustersFromDb(String geohash, int level) {
		return switch (level) {
			case 1 -> sidoAreasRepository.findByGeohashPrefix(geohash);
			case 2 -> siggAreasRepository.findByGeohashPrefix(geohash);
			default -> throw new InvalidMapLevelException();
		};
	}

	private void cache(List<DiaryClusterResponseDto> clusters, String geohash, int level) {
		String key = CacheKeyGenerator.clusterCacheKey(geohash, level);

		try {
			String json = objectMapper.writeValueAsString(clusters);
			redisTemplate.opsForValue().set(key, json, RedisTTLPolicy.CLUSTER_TTL);
		} catch (Exception e) {
			log.warn("클러스터 캐시 저장 실패 (geo={}, level={})", key, clusters.size(), e);
			throw new RuntimeException(e);
		}
	}
}
