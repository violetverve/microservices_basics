package com.example;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
public class DistributedMapLocks {
    private static final int INCREMENTS = 10_000;
    private static final int THREADS = 3;

    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("test-cluster");
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        performIncrement("optimistic_lock", hazelcastInstance, DistributedMapLocks::optimisticIncrement);
        performIncrement("pessimistic_lock", hazelcastInstance, DistributedMapLocks::pessimisticIncrement);
        performIncrement("no_lock", hazelcastInstance, (map, key) -> map.put(key, map.get(key) + 1));
    }

    private static void performIncrement(String mapName, HazelcastInstance instance, IncrementStrategy strategy) {
        IMap<String, Integer> map = instance.getMap(mapName);
        map.put(mapName, 0);

        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    strategy.increment(map, mapName);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("Key value with " + mapName + ": " + map.get(mapName));
    }

    @FunctionalInterface
    interface IncrementStrategy {
        void increment(IMap<String, Integer> map, String key);
    }

    private static void optimisticIncrement(IMap<String, Integer> map, String key) {
        boolean updated = false;
        while (!updated) {
            Integer currentValue = map.get(key);
            updated = map.replace(key, currentValue, currentValue + 1);
        }
    }

    private static void pessimisticIncrement(IMap<String, Integer> map, String key) {
        map.lock(key);
        try {
            map.put(key, map.get(key) + 1);
        } finally {
            map.unlock(key);
        }
    }
}