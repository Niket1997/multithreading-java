package org.nik;

public class SafeStack {
    int[] arr;
    int stackTop;
    final Object lock;

    public SafeStack(int capacity) {
        this.arr = new int[capacity];
        this.stackTop = -1;
        this.lock = new Object();
    }

    public boolean push(int value) {
        synchronized (lock) {
            if (isFull()) return false;
            arr[++stackTop] = value;
            return true;
        }
    }

    // public synchronized int pop() --> this is also valid,
    // it uses the current object of SafeStack class as lock
    // every object in java can be used as lock, except primitives like int
    public int pop() {
        synchronized (lock) { // using same lock as above
            if (isEmpty()) return Integer.MIN_VALUE;
            int returnVal = arr[stackTop];
            arr[stackTop] = Integer.MIN_VALUE;
            stackTop--;
            return returnVal;
        }
    }

    public boolean isEmpty() {
        return stackTop == -1;
    }

    public boolean isFull() {
        return stackTop == arr.length - 1;
    }

    public static void main(String[] args) {
        System.out.println("starting main thread");
        SafeStack stack = new SafeStack(10);

        Thread pusherThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(stack.push(i));
            }
        }, "pusher");

        Thread popperThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(stack.pop());
            }
        }, "popper");

        pusherThread.start();
        popperThread.start();

        System.out.println("exiting main thread");
    }
}
