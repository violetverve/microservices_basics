package com.example;

import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.springframework.http.MediaType;


@Service
public class FacadeService {
    private static final String QUEUE_NAME = "messages-queue";

    Logger logger = LoggerFactory.getLogger(FacadeService.class);
    private final List<WebClient> loggingWebClients;
    private final List<WebClient> messagesWebClients;
    private final IQueue<Message> messageQueue;

    public FacadeService()  {
        loggingWebClients = List.of(
                WebClient.create("http://localhost:8082"),
                WebClient.create("http://localhost:8083"),
                WebClient.create("http://localhost:8084")
        );

        messagesWebClients = List.of(
                WebClient.create("http://localhost:8085"),
                WebClient.create("http://localhost:8086")
        );

        Config config = new Config();
        config.getSerializationConfig().getCompactSerializationConfig().addSerializer(new MessageCompactSerializer());

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        messageQueue = hazelcastInstance.getQueue(QUEUE_NAME);

    }

    public Mono<Void> addMessage(PayloadText text) {

        var msg = new Message(UUID.randomUUID(), text.txt);

        var loggingWebClient = getRandomLoggingClient();

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

        var loggingWebClient = getRandomLoggingClient();
        var logValuesMono = loggingWebClient.get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class);


        WebClient messagesWebClient = getRandomMessagesWebClient();
        var messageMono = messagesWebClient.get()
                        .uri("/message")
                        .retrieve()
                        .bodyToMono(String.class);

        return logValuesMono.zipWith(messageMono,
                        (logValues, message) -> logValues + ": " + message)
                .onErrorReturn("Error in logging or messages fetching");
    }


    private WebClient getRandomLoggingClient() {
        Random random = new Random();
        int randomIndex = random.nextInt(loggingWebClients.size());
        return loggingWebClients.get(randomIndex);
    }

    private WebClient getRandomMessagesWebClient() {
        Random random = new Random();
        int randomIndex = random.nextInt(messagesWebClients.size());
        return messagesWebClients.get(randomIndex);
    }
}