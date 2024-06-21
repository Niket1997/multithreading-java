package org.nik;

public class ThreadWithRunnableLambda {
    public static void main(String[] args) {
        System.out.println("starting main thread");
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("inside thread: " + Thread.currentThread().getName() + ": " + i);
            }
        }, "thread3");
        t3.start();
        System.out.println("exiting main thread");
    }
}
