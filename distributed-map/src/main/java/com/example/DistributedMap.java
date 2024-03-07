package com.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.config.Config;

public class DistributedMap {

    private static final int TOTAL_VALUES = 1000;

    public static void main(String[] args) {
        Config config = new Config();
        config.setClusterName("test-cluster");

        HazelcastInstance hazelcastNode1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastNode2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastNode3 = Hazelcast.newHazelcastInstance(config);

        var distributedMap = hazelcastNode1.getMap("distributedMap");

        for (int i = 0; i < TOTAL_VALUES; ++i) {
            distributedMap.put(i, i);
        }

        // Example: Safely shutting down only two nodes
        shutdownNodesSafely(hazelcastNode2, hazelcastNode3);

        // Example: Terminating only two nodes immediately
        // terminateNodesImmediately(hazelcastNode2, hazelcastNode3);
    }

    private static void shutdownNodesSafely(HazelcastInstance... instances) {
        System.out.println("Shutting down selected nodes safely...");
        for (HazelcastInstance instance : instances) {
            if (instance != null) {
                instance.shutdown();
            }
        }
    }

    private static void terminateNodesImmediately(HazelcastInstance... instances) {
        System.out.println("Terminating selected nodes immediately...");
        for (HazelcastInstance instance : instances) {
            if (instance != null) {
                instance.getLifecycleService().terminate();
            }
        }
    }
}