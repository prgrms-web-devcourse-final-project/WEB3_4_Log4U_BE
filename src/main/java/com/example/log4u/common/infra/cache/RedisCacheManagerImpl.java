package com.example.log4u.common.infra.cache;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisCacheManagerImpl implements CacheManager {

    private final RedisTemplate<String, String> redisTemplate;

    public void cache(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }
}
