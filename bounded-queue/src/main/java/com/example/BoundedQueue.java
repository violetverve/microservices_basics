package com.example;

import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class BoundedQueue {
    private static final int TOTAL_ITEMS = 100;
    private static final int ITEMS_PER_CONSUMER = 50;
    private static final int NUMBER_OF_CONSUMERS = 2;
    private static final String BOUNDED_QUEUE = "boundedQueue";
    private static final Boolean CONSUMERS_PRESENT = false;

    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("test-cluster");

        QueueConfig queueConfig = config.getQueueConfig(BOUNDED_QUEUE);
        queueConfig.setMaxSize(10);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        IQueue<Integer> queue = hazelcastInstance.getQueue(BOUNDED_QUEUE);

        Thread producer = new Thread(() -> produce(queue));
        producer.start();

        Thread[] consumers = new Thread[NUMBER_OF_CONSUMERS];
        if (CONSUMERS_PRESENT) {
            for (int i = 0; i < NUMBER_OF_CONSUMERS; i++) {
                final int consumerId = i + 1;
                consumers[i] = new Thread(() -> consume(queue, consumerId));
                consumers[i].start();
            }
        }

        try {
            producer.join();
            for (Thread consumer : consumers) {
                consumer.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted: " + e.getMessage());
        }
    }

    private static void produce(IQueue<Integer> queue) {
        for (int i = 1; i <= TOTAL_ITEMS; i++) {
            boolean offered = queue.offer(i);
            if (offered) {
                System.out.println("Producer successfully offered item " + i + " to the queue.");
            } else {
                System.out.println("Producer could not offer item " + i + " because the queue is full.");
            }
        }
    }

    private static void consume(IQueue<Integer> queue, int consumerId) {
        for (int j = 0; j < ITEMS_PER_CONSUMER; j++) {
            try {
                Integer value = queue.take();
                System.out.println("Consumer " + consumerId + " successfully took item " + value + " from the queue.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Consumer " + consumerId + " was interrupted and could not complete consumption.");
            }
        }
    }

}
