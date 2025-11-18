package com.example.log4u.common.infra.cache;

import java.time.Duration;

public interface CacheManager {

    void cache(String key, String value, Duration ttl);

    String get(String key);

    void evict(String key);
}
