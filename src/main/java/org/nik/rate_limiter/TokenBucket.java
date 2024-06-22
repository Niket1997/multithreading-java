package org.nik.rate_limiter;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket implements IRateLimiter {
    // max requests allowed per unit time
    private final Interval maxAllowed;

    // fill rate per unit time
    private final Interval fillRate;

    // current available tokens
    private final AtomicLong currentAvailable;

    // last timestamp when the request was made
    private long lastRefillTime;

    public TokenBucket(Interval maxAllowed, Interval fillRate, int currentAvailable) {
        this.maxAllowed = maxAllowed;
        this.fillRate = fillRate;
        this.currentAvailable = new AtomicLong(currentAvailable);
        this.lastRefillTime = System.currentTimeMillis();
    }

    @Override
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        long tokensToBeAdded = fillRate.getDifference(currentTime, lastRefillTime);
        if (tokensToBeAdded > 0) {
            currentAvailable.set(Math.min(maxAllowed.getVal(), currentAvailable.addAndGet(tokensToBeAdded)));
            lastRefillTime = currentTime;
        }

        if (currentAvailable.get() > 0) {
            currentAvailable.decrementAndGet();
            return true;
        }

        return false;
    }
}
