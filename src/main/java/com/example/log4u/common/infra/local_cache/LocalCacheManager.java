package com.example.log4u.common.infra.local_cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.log4u.common.infra.local_cache.message_publisher.MessagePublisher;
import com.example.log4u.common.infra.local_cache.message_subscriber.MessageSubscriber;
import com.github.benmanes.caffeine.cache.Cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCacheManager {

    private static final String INVALIDATE_CACHE_CHANNEL = "invalidate-cache";

    private final MessagePublisher messagePublisher;
    private final MessageSubscriber messageSubscriber;

    private final Map<String, Object> localCaches = new HashMap<>();

    @PostConstruct
    public void init() {
        setMessageListenerOfSubscriber();
    }

    public void addLocalCache(String localCacheName, Object localCache) {
        localCaches.put(localCacheName, localCache);
    }

    public void publishInvalidateCacheMessage(String localCacheName, String key) {
        String message = localCacheName + ":" + key;
        messagePublisher.publish(INVALIDATE_CACHE_CHANNEL, message);
    }

    private void setMessageListenerOfSubscriber() {
        messageSubscriber.addMessageListener(
            INVALIDATE_CACHE_CHANNEL,
            (message, pattern) -> invalidateCache(message.getBody())
        );
    }

    private void invalidateCache(byte[] message) {
        String payload = messageSubscriber.parseMessage(message);
        String[] parts = payload.split(":", 2);

        String cacheName = parts[0];
        String key       = parts[1];
        if (!localCaches.containsKey(cacheName)) {
            throw new IllegalArgumentException("Invalid local cache name: " + cacheName);
        }

        Cache<String, Object> localCache = (Cache<String, Object>) localCaches.get(cacheName);
        localCache.invalidate(key);
    }

}

