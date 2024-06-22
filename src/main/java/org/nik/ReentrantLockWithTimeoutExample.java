package org.nik;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockWithTimeoutExample {
    private final ReentrantLock lock;
    int count;

    public ReentrantLockWithTimeoutExample() {
        lock = new ReentrantLock();
        count = 0;
    }

    public boolean increment() throws InterruptedException {
        boolean isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
        if (isLockAcquired) {
            try {
                count++;
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                lock.unlock();
            }
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        ReentrantLockWithTimeoutExample example = new ReentrantLockWithTimeoutExample();

        Runnable runnable = () -> {
            try {
                System.out.println(example.increment());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread t1 = new Thread(runnable, "t1");
        Thread t2 = new Thread(runnable, "t2");
        t1.start();
        t2.start();
    }
}
