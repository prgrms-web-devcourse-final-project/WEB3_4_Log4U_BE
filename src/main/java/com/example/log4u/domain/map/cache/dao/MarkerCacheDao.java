package com.example.log4u.domain.map.cache.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryGeoHashRepository;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.cache.CacheKeyGenerator;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkerCacheDao {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private final DiaryRepository diaryRepository;
	private final DiaryGeoHashRepository diaryGeoHashRepository;

	public List<DiaryMarkerResponseDto> load(String geohash) {
		String key = CacheKeyGenerator.markerCacheKey(geohash);
		try {
			String value = redisTemplate.opsForValue().get(key);
			if (value == null) {
				return null;
			}
			return objectMapper.readValue(value, new TypeReference<>() {
			});
		} catch (Exception e) {
			log.warn("[REDIS][GET][ERR] key={}", key, e);
			throw new RuntimeException(e);
		}
	}

	public List<DiaryMarkerResponseDto> loadAndCache(String geohash) {
		List<Long> diaryIds = diaryGeoHashRepository.findDiaryIdByGeohash(geohash);
		if (diaryIds.isEmpty()) {
			cache(Collections.emptyList(), geohash);
			return Collections.emptyList();
		}
		List<Diary> diaries = diaryRepository.findAllById(diaryIds);
		List<DiaryMarkerResponseDto> markers = diaries.stream()
			.map(DiaryMarkerResponseDto::of)
			.toList();
		cache(markers, geohash);
		return markers;
	}

	private void cache(List<DiaryMarkerResponseDto> markers, String geohash) {
		String key = CacheKeyGenerator.markerCacheKey(geohash);
		try {
			String json = objectMapper.writeValueAsString(markers);
			redisTemplate.opsForValue().set(key, json, RedisTTLPolicy.DIARY_TTL);
		} catch (Exception e) {
			log.warn("[REDIS][SET][ERR] key={} count={}", key, markers.size(), e);
			throw new RuntimeException(e);
		}
	}

	public void evict(String geohash) {
		String key = CacheKeyGenerator.markerCacheKey(geohash);
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
