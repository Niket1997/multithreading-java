package org.nik;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolExecutorForLoopExample {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        Runnable runnable = () -> {
            try {
                System.out.println("started thread: " + Thread.currentThread().getName());
                Thread.sleep(3000);
                System.out.println("completed thread: " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(runnable);
        }

        executor.shutdown();
    }
}
