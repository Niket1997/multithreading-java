package org.nik.rate_limiter;

public interface IRateLimiter {
    boolean tryAcquire();
}
