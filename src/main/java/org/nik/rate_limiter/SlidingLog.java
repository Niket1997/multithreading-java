package org.nik.rate_limiter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingLog implements IRateLimiter {
    private final long slidingWindowMillis;
    private final long slidingWindowThreshold;
    private final ConcurrentHashMap<String, ArrayDeque<Long>> logMap;

    public SlidingLog(long slidingWindowMillis, long slidingWindowThreshold) {
        this.slidingWindowMillis = slidingWindowMillis;
        this.slidingWindowThreshold = slidingWindowThreshold;
        logMap = new ConcurrentHashMap<>();
    }


    @Override
    public boolean tryAcquire(@NotNull String key) {
        synchronized (key) {
            long currentTime = System.currentTimeMillis();
            cleanupOldEntries(key, currentTime);
            logMap.putIfAbsent(key, new ArrayDeque<>());
            ArrayDeque<Long> log = logMap.get(key);
            if (log.size() >= slidingWindowThreshold) {
                return false;
            }
            log.push(currentTime);
            return true;
        }
    }

    private void cleanupOldEntries(String key, long currentTime) {
        ArrayDeque<Long> oldEntries = logMap.get(key);
        if (oldEntries != null) {
            while (!oldEntries.isEmpty() && (currentTime - oldEntries.peekFirst()) > slidingWindowMillis) {
                oldEntries.removeFirst();
            }
        }
    }

    public static void main(String[] args) {
        SlidingLog rateLimiter = new SlidingLog(5000, 5);
        String key = "user12345";

        for (int i = 0; i < 10; i++) {
            boolean result = rateLimiter.tryAcquire(key);
            System.out.println(result ? "Acquired" : "Failed");
        }
    }
}
