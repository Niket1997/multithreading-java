package org.nik;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockHashMapExample {
    private final ReentrantReadWriteLock lock;
    private final HashMap<Integer, Integer> map;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final ReentrantReadWriteLock.ReadLock readLock;

    public ReentrantReadWriteLockHashMapExample() {
        lock = new ReentrantReadWriteLock();
        map = new HashMap<>();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    // If no threads are reading or writing, only one thread can acquire the write lock.
    public void put(int key, int value) {
        try {
            writeLock.lock();
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    // If no threads are reading or writing, only one thread can acquire the write lock.
    public int remove(int key) {
        try {
            writeLock.lock();
            return map.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    // multiple threads can read the data if no write is happening
    public int get(int key) {
        try {
            readLock.lock();
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    // multiple threads can read the data if no write is happening
    public boolean containsKey(int key) {
        try {
            readLock.lock();
            return map.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
}
