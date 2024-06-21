package org.nik;

class ThreadClass extends Thread {
    public ThreadClass(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("inside thread: " + Thread.currentThread().getName() + " " + i);
        }
    }
}

public class ExtendingThreadClass {
    public static void main(String[] args) {
        System.out.println("starting main thread");
        Thread thread1 = new ThreadClass("Thread1");
        thread1.setDaemon(true);
        thread1.start();
        System.out.println("exiting main thread");
    }
}
