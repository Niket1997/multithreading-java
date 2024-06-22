package org.nik.rate_limiter;

import java.util.concurrent.atomic.AtomicLong;

public class LeakyBucket implements IRateLimiter {
    // max water that can be present in bucket
    private final long threshold;

    // rate at which water is leaked from bucket
    private final Interval leakRate;

    // last timestamp when water was leaked
    private long lastLeakTimestamp;

    // current level of water
    private final AtomicLong water;

    public LeakyBucket(long threshold, Interval leakRate) {
        this.threshold = threshold;
        this.leakRate = leakRate;
        water = new AtomicLong(0);
    }

    @Override
    public boolean tryAcquire() {
        long currentTimeMillis = System.currentTimeMillis();

        long leakedAmount = leakRate.getDifference(currentTimeMillis, lastLeakTimestamp);

        if (leakedAmount > threshold) {
            water.addAndGet(-leakedAmount);
            lastLeakTimestamp = currentTimeMillis;
        }

        if (water.get() < 0) {
            water.set(0);
        }

        if (water.get() < threshold) {
            water.incrementAndGet();
            return true;
        }

        return false;
    }
}
