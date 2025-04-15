package com.example.log4u.common.redis;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisDao {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	public <T> void setList(String key, List<T> list, Duration ttl) {
		try {
			redisTemplate.opsForValue().set(key, list, ttl);
		} catch (Exception e) {
			log.warn("Redis 캐시 저장 실패 key: {}", key, e);
		}
	}

	public <T> List<T> getList(String key, Class<T> clazz) {
		try {
			Object raw = redisTemplate.opsForValue().get(key);
			if (raw == null) return null;

			String json = objectMapper.writeValueAsString(raw);
			JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
			return objectMapper.readValue(json, type);
		} catch (Exception e) {
			log.warn("Redis 캐시 조회 실패 key: {}", key, e);
			return null;
		}
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
