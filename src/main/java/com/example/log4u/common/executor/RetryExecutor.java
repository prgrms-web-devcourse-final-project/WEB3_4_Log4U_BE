package com.example.log4u.common.executor;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.exception.MaxRetryExceededException;

@Component
public class RetryExecutor {

    int MAX_RETRY_CNT = 10;
    int INITIAL_DELAY_MSEC = 1_000;
    int MAX_DELAY_MSEC = 100_000;

    public <T> T runWithRetry(Supplier<T> task) {
        for (int attempt = 0; attempt < MAX_RETRY_CNT; attempt++) {
            T result = task.get();
            if (result != null) {
                return result;
            }
            backoff(attempt);
        }
        throw new MaxRetryExceededException();
    }

    private void backoff(int attempt) {
        try {
            int delay = Math.min(
                    INITIAL_DELAY_MSEC * (int) Math.pow(2, attempt - 1),
                    MAX_DELAY_MSEC);

            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
