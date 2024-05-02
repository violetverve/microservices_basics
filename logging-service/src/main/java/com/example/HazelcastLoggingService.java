package com.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Primary
public class HazelcastLoggingService implements LoggingService{

    @Value("${loggingMapName}")
    private String loggingMapName;

    private final HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
    private Map<UUID, String> messages;

    @PostConstruct
    public void init() {
        messages = hzInstance.getMap(loggingMapName);
    }

    @Override
    public void addToLog(Message msg) {
        messages.put(msg.id, msg.txt);
    }

    @Override
    public Map<UUID, String> log() {
        return messages;
    }
}
