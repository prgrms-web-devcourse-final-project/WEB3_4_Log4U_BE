package com.example.log4u.domain.map.cache.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.cache.CacheKeyGenerator;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiaryCacheDao {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	public Set<Long> getDiaryIdSetFromCache(String geohash) {
		String key = CacheKeyGenerator.diaryIdSetKey(geohash);
		try {
			Set<Object> raw = redisTemplate.opsForSet().members(key);
			if (raw == null)
				return Collections.emptySet();

			return raw.stream()
				.map(obj -> Long.parseLong(obj.toString()))
				.collect(Collectors.toSet());
		} catch (Exception e) {
			log.warn("diaryId Set 조회 실패 (key: {})", key, e);
			return Collections.emptySet();
		}
	}

	public void cacheDiaryIdSetByGeohash(String geohash, List<Long> diaryIds) {
		if (diaryIds == null || diaryIds.isEmpty())
			return;

		String key = CacheKeyGenerator.diaryIdSetKey(geohash);
		try {
			redisTemplate.opsForSet().add(key, diaryIds.toArray());
			redisTemplate.expire(key, RedisTTLPolicy.DIARY_ID_SET_TTL);
		} catch (Exception e) {
			log.warn("diaryId Set 저장 실패 (key: {})", key, e);
		}
	}

	public List<DiaryMarkerResponseDto> getDiariesFromCacheBulk(List<Long> diaryIds) {
		List<String> keys = diaryIds.stream()
			.map(CacheKeyGenerator::diaryKey)
			.toList();
		List<Object> values = redisTemplate.opsForValue().multiGet(keys);

		List<DiaryMarkerResponseDto> result = new ArrayList<>();
		for (Object raw : values) {
			if (raw != null) {
				try {
					result.add(objectMapper.readValue(raw.toString(), DiaryMarkerResponseDto.class));
				} catch (Exception e) {
					log.warn("Diary 캐시 역직렬화 실패", e);
				}
			}
		}
		return result;
	}

	public void cacheAllDiaries(List<DiaryMarkerResponseDto> dtos) {
		try {
			Map<String, String> map = new HashMap<>();
			for (DiaryMarkerResponseDto dto : dtos) {
				String key = CacheKeyGenerator.diaryKey(dto.diaryId());
				String value = objectMapper.writeValueAsString(dto);
				map.put(key, value);
			}
			redisTemplate.opsForValue().multiSet(map);

			// TTL은 multiSet에는 개별로 설정 불가 → 옵션: pipeline 사용
			// 또는 각 key에 대해 expire 호출 반복 필요
			for (String key : map.keySet()) {
				redisTemplate.expire(key, RedisTTLPolicy.DIARY_TTL);
			}
		} catch (Exception e) {
			log.warn("diary bulk 캐시 저장 실패", e);
		}
	}


	public void evictDiaryIdFromCache(String geohash, Long diaryId) {
		String key = CacheKeyGenerator.diaryIdSetKey(geohash);
		try {
			redisTemplate.opsForSet().remove(key, diaryId);
		} catch (Exception e) {
			log.warn("diaryId 제거 실패 (key: {}, diaryId: {})", key, diaryId, e);
		}
	}

	public void evictDiaryFromCache(Long diaryId) {
		String key = CacheKeyGenerator.diaryKey(diaryId);
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.warn("diary 캐시 삭제 실패 (key: {})", key, diaryId, e);
		}
	}
}
