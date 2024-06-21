package org.nik;

class ThreadTwo implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("inside thread: " + Thread.currentThread().getName() + ", " + i);
        }
    }
}

public class ImplementingRunnableInterface {
    public static void main(String[] args) {
        System.out.println("starting main thread");
        Thread t1 = new Thread(new ThreadTwo(), "threadTwo");
        t1.start();
        System.out.println("exiting main thread");
    }
}
