package com.example.log4u.domain.map.cache.dao;

import java.util.Collections;
import java.util.List;
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
			log.error("diaryId Set 조회 실패 (key: {})", key, e);
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
			log.error("diaryId Set 저장 실패 (key: {})", key, e);
		}
	}

	public DiaryMarkerResponseDto getDiaryFromCache(Long diaryId) {
		String key = CacheKeyGenerator.diaryKey(diaryId);
		try {
			Object raw = redisTemplate.opsForValue().get(key);
			if (raw == null) return null;

			return objectMapper.readValue(raw.toString(), DiaryMarkerResponseDto.class);
		} catch (Exception e) {
			log.error("단건 diary 조회 실패 (key: {})", key, e);
			return null;
		}
	}

	public void cacheDiary(Long diaryId, DiaryMarkerResponseDto dto) {
		String key = CacheKeyGenerator.diaryKey(diaryId);
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, RedisTTLPolicy.DIARY_TTL);
		} catch (Exception e) {
			log.error("단건 diary 캐시 저장 실패 (key: {})", key, e);
		}
	}

	public void evictDiaryIdFromCache(String geohash, Long diaryId) {
		String key = CacheKeyGenerator.diaryIdSetKey(geohash);
		try {
			redisTemplate.opsForSet().remove(key, diaryId);
		} catch (Exception e) {
			log.error("diaryId 제거 실패 (key: {}, diaryId: {})", key, diaryId, e);
		}
	}

	public void evictDiaryFromCache(Long diaryId) {
		String key = CacheKeyGenerator.diaryKey(diaryId);
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.error("diary 캐시 삭제 실패 (key: {})", key, diaryId, e);
		}
	}

}
