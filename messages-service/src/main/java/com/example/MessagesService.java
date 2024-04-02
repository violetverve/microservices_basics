package com.example;

import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessagesService {
    private static final Logger logger = LoggerFactory.getLogger(MessagesService.class);
    private static final String QUEUE_NAME = "messages-queue";
    private final List<Message> messageList = new CopyOnWriteArrayList<>();
    private final IQueue<Message> messageQueue;

    public MessagesService() {
        Config config = new Config();
        config.getSerializationConfig().getCompactSerializationConfig().addSerializer(new MessageCompactSerializer());

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        messageQueue = hazelcastInstance.getQueue(QUEUE_NAME);
    }

    @Scheduled(fixedDelay = 1000)
    public void consumeMessages() {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            if (message != null) {
                logger.info(message.toString());
                messageList.add(message);
            }
        }
    }

    public List<Message> getMessageList() {
        return messageList;
    }

}
