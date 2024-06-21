package org.nik;

import java.util.LinkedList;

public class BlockingQueue {
    private final LinkedList<Integer> queue;
    private final int capacity;

    public BlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public void put(int item) {
        synchronized (queue) {
            while (queue.size() == capacity) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            queue.add(item);
            queue.notifyAll();
        }
    }

    public int remove() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            int top = queue.poll();
            queue.notifyAll();
            return top;
        }
    }

    public static void main(String[] args) {
        System.out.println("starting main thread");
        BlockingQueue blockingQueue = new BlockingQueue(10);

        Thread publisherThread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                blockingQueue.put(i);
            }
        }, "publisher");

        Thread consumerThread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                int top = blockingQueue.remove();
                System.out.println(top);
            }
        }, "consumer");

        publisherThread.start();
        consumerThread.start();
        System.out.println("exiting main thread");
    }
}
