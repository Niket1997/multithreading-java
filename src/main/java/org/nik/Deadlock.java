package org.nik;

public class Deadlock {
    public static void main(String[] args) {
        System.out.println("main is starting");

        Object lockOne = new Object();
        Object lockTwo = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lockOne) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockTwo) {
                    System.out.println("lock acquired");
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lockTwo) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockOne) {
                    System.out.println("lock acquired");
                }
            }
        }, "t2");

        t1.start();
        t2.start();
        System.out.println("main is exiting ");
    }
}
