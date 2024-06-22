package org.nik.circuitbreaker;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Gateway {
    public static void main(String[] args) {
        CircuitBreaker breaker = getCircuitBreaker();

        Runnable runnable = () -> {
            if (breaker.tryAcquire()) {
                try {
                    // do operation here
                    Thread.sleep(1000);
                    int randInt = new Random().nextInt(100);
                    if (randInt < 10) {
                        throw new RuntimeException("exception occurred");
                    }
                    breaker.markSuccess();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    breaker.markFailure();
                }
            } else {
                System.out.println("couldn't acquire breaker");
            }
        };

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            executor.execute(runnable);
        }
        executor.shutdown();
    }

    private static CircuitBreaker getCircuitBreaker() {
        int failureThreshold = 5;
        Interval failureInterval = new Interval(TimeUnit.MINUTES, 2);
        long failureDelay = 1000; // circuit breaker will be in OPEN state at least for 1 second

        int successThreshold = 2;
        Interval successInterval = new Interval(TimeUnit.MINUTES, 1);

        return new CircuitBreaker(
                failureThreshold,
                failureInterval,
                failureDelay,
                successThreshold,
                successInterval
        );
    }
}
