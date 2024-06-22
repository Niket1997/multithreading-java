package org.nik.circuitbreaker;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CircuitBreaker {
    // set the state of the circuit breaker to open once this threshold is breached
    private final int failureThreshold;
    private final Interval failureInterval;

    // set the state of the circuit breaker to HALF_OPEN state, after this time is passed
    // from the time the state was set to OPEN
    private final long failureDelay;

    // once the circuit breaker is in HALF_OPEN state,
    // set it to CLOSED state if successThreshold number of
    // requests are succeeded
    private final int successThreshold;
    private final Interval successInterval;

    // these store counts at minute level, at each minute what was the
    // failure or success count
    private final ConcurrentHashMap<Long, Integer> failureCounts;
    private final ConcurrentHashMap<Long, Integer> successCounts;

    // create a lock to handle state changes
    private final ReentrantLock lock;

    // maintain the state of the circuit breaker
    private State state;

    // store when last failure occurred
    private long lastFailedAt;

    public CircuitBreaker(int failureThreshold, Interval failureInterval, long failureDelay, int successThreshold, Interval successInterval) {
        this.failureThreshold = failureThreshold;
        this.failureInterval = failureInterval;
        this.failureDelay = failureDelay;
        this.successThreshold = successThreshold;
        this.successInterval = successInterval;
        failureCounts = new ConcurrentHashMap<>();
        successCounts = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
        state = State.CLOSED;
        lastFailedAt = 0;
    }

    // if the state is CLOSED, return true
    // if the state is HALF_OPEN, return true
    // if the state is OPEN,
    public boolean tryAcquire() {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailedAt < failureDelay) {
                return false;
            }
            lock.lock();
            try {
                if (state == State.OPEN) {
                    System.out.println("setting the state to HALF_OPEN");
                    state = State.HALF_OPEN; // attempt retry
                }
            } finally {
                lock.unlock();
            }
        }
        return true;
    }

    // client marks the request as success
    // this will be called by threads which were
    // able to acquire the lock by calling above method
    // i.e. at that time, state was either CLOSED or HALF_OPEN
    public void markSuccess() {
        lock.lock();
        try {
            System.out.println("marking success");
            long currentMinute = Instant.now().getEpochSecond() / 60;
            successCounts.put(currentMinute, successCounts.getOrDefault(currentMinute, 0) + 1);
            if (state == State.HALF_OPEN && isSuccessThresholdCrossed(currentMinute)) {
                System.out.println("setting the state to CLOSED");
                state = State.CLOSED;
            }
        } finally {
            lock.unlock();
        }
    }

    // if the state is CLOSED, then mark the state as OPEN, if threshold breached
    // if the state is HALF_OPEN, then mark the state as OPEN
    public void markFailure() {
        lock.lock();
        try {
            System.out.println("marking failure");
            lastFailedAt = Instant.now().getEpochSecond();
            long currentMinute = lastFailedAt / 60;
            failureCounts.put(currentMinute, failureCounts.getOrDefault(currentMinute, 0) + 1);
            if (state == State.HALF_OPEN || (state == State.CLOSED && isFailureThresholdBreached(currentMinute))) {
                System.out.println("setting the state to OPEN");
                state = State.OPEN;
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isFailureThresholdBreached(long currentMinute) {
        int total = 0;
        for (long time = currentMinute; time > currentMinute - failureInterval.getVal(); time--) {
            total += failureCounts.getOrDefault(time, 0);
        }
        return total >= failureThreshold;
    }

    private boolean isSuccessThresholdCrossed(long currentMinute) {
        int total = 0;
        for (long time = currentMinute; time > currentMinute - successInterval.getVal(); time--) {
            total += successCounts.getOrDefault(time, 0);
        }
        return total >= successThreshold;
    }
}

