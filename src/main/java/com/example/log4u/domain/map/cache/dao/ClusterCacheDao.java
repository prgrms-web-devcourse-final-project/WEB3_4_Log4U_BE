package com.example.log4u.domain.map.cache.dao;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.cache.CacheKeyGenerator;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClusterCacheDao {

	private final RedisTemplate<String, List<DiaryClusterResponseDto>> diaryClusterRedisTemplate;

	public Optional<List<DiaryClusterResponseDto>> getDiaryCluster(String geohash, int level) {
		try {
			String key = CacheKeyGenerator.clusterCacheKey(geohash, level);
			List<DiaryClusterResponseDto> cached = diaryClusterRedisTemplate.opsForValue().get(key);
			return Optional.ofNullable(cached);
		} catch (Exception e) {
			log.warn("클러스터 캐시 조회 실패 (geo={}, level={})", geohash, level, e);
			return Optional.empty();
		}
	}

	public void setDiaryCluster(String geohash, int level, List<DiaryClusterResponseDto> data, Duration ttl) {
		try {
			String key = CacheKeyGenerator.clusterCacheKey(geohash, level);
			diaryClusterRedisTemplate.opsForValue().set(key, data, ttl);
		} catch (Exception e) {
			log.warn("클러스터 캐시 저장 실패 (geo={}, level={})", geohash, level, e);
		}
	}
}
