package com.example.log4u.common.executor;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.example.log4u.common.infra.cache.CacheManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final CacheManager cacheManager;

    public void runWithLock(String lockKey, Runnable task) {
        runWithLock(lockKey, () -> task);
    }

    public <T> T runWithLock(String lockKey, Supplier<T> task) {
        if (!cacheManager.tryLock(lockKey)) {
            return null;
        }
        try {
            return task.get();
        } finally {
            cacheManager.releaseLock(lockKey);
        }
    }
}
