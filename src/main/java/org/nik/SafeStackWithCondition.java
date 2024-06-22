package org.nik;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SafeStackWithCondition {
    private final int[] stack;
    private int idx;
    private final ReentrantLock lock;
    private final Condition stackFullCondition;
    private final Condition stackEmptyCondition;

    public SafeStackWithCondition(int capacity) {
        stack = new int[capacity];
        idx = -1;
        lock = new ReentrantLock();
        stackFullCondition = lock.newCondition();
        stackEmptyCondition = lock.newCondition();
    }

    public void push(int value) {
        try {
            lock.lock();
            while (idx == stack.length - 1) {
                stackFullCondition.await();
            }
            stack[++idx] = value;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            stackEmptyCondition.signalAll();
            lock.unlock();
        }
    }

    public int pop() {
        try {
            lock.lock();
            while (idx == -1) {
                stackEmptyCondition.await();
            }
            int value = stack[idx];
            stack[idx] = Integer.MIN_VALUE;
            idx--;
            return value;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return Integer.MIN_VALUE;
        } finally {
            stackFullCondition.signalAll();
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        SafeStackWithCondition stack = new SafeStackWithCondition(10);

        Thread threadPusher = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                stack.push(i);
            }
        }, "pusher");

        Thread threadPopper = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(stack.pop());
            }
        }, "popper");

        threadPusher.start();
        threadPopper.start();
    }
}
