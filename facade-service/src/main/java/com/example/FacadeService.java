package com.example;

import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.springframework.http.MediaType;

@Service
public class FacadeService {

    @Value("${queueName}")
    private String queueName;

    Logger logger = LoggerFactory.getLogger(FacadeService.class);

    private IQueue<Message> messageQueue;

    DiscoveryClient discoveryClient;

    public FacadeService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @PostConstruct
    public void init() {
        Config config = new Config();
        config.getSerializationConfig().getCompactSerializationConfig().addSerializer(new MessageCompactSerializer());

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        messageQueue = hazelcastInstance.getQueue(queueName);
    }


    public Mono<Void> addMessage(PayloadText text) {

        var msg = new Message(UUID.randomUUID(), text.txt);


        var loggingWebClient = getRandomClient("logging-service");

        logger.info(loggingWebClient.toString());

        Mono<Void> logToLoggingService = loggingWebClient
                .post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);


        Mono<Void> offerToMessageQueue = Mono.fromRunnable(() -> {
            boolean added = messageQueue.offer(msg);
            if (!added) {
                throw new RuntimeException("Failed to offer message to queue");
            }
        }).then();

        return Mono.when(logToLoggingService, offerToMessageQueue).then();
    }

    public Mono<String> messages() {

        Mono<String> logValuesMono = getRandomClient("logging-service").get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> messageMono = getRandomClient("messages-service").get()
                .uri("/message")
                .retrieve()
                .bodyToMono(String.class);

        return logValuesMono.zipWith(messageMono,
                        (logValues, message) -> logValues + ": " + message)
                .onErrorReturn("Error in logging or messages fetching");
    }


    WebClient getRandomClient(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        return WebClient.create(instances.get(new Random().nextInt(instances.size())).getUri().toString());
    }

}