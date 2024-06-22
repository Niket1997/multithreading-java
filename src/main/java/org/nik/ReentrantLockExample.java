package org.nik;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
    private final ReentrantLock lock;
    private int count;

    public ReentrantLockExample() {
        lock = new ReentrantLock();
        count = 0;
    }

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantLockExample example = new ReentrantLockExample();
        example.increment();
        System.out.println(example.count);
    }
}
