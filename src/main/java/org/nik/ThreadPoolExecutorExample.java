package org.nik;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolExecutorExample {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        Runnable runnable = () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);

        if (executor.getPoolSize() == 2) {
            System.out.println("pool size is: " + executor.getPoolSize());
        }

        if (executor.getQueue().size() == 1) {
            System.out.println("queue size is: " + executor.getQueue().size());
        }

        executor.shutdown();
    }
}
