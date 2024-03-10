package com.example;

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

    Logger logger = LoggerFactory.getLogger(FacadeService.class);

    private final List<WebClient> loggingWebClients;
    private final WebClient messagesWebClient;

    public FacadeService()  {
        loggingWebClients = List.of(
                WebClient.create("http://localhost:8082"),
                WebClient.create("http://localhost:8083"),
                WebClient.create("http://localhost:8084")
        );

        messagesWebClient = WebClient.create("http://localhost:8081");
    }

    public Mono<Void> addMessage(PayloadText text) {

        var msg = new Message(UUID.randomUUID(), text.txt);

        var loggingWebClient = getRandomLoggingClient();

        logger.info(loggingWebClient.toString());

        return  loggingWebClient.post()
                                .uri("/log")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(msg), Message.class)
                                .retrieve()
                                .bodyToMono(Void.class);
    }


    public Mono<String> messages() {
        var loggingWebClient = getRandomLoggingClient();

        var logValuesMono = loggingWebClient.get()
                                            .uri("/log")
                                            .retrieve()
                                            .bodyToMono(String.class);

        var messageMono = messagesWebClient.get()
                                            .uri("/message")
                                            .retrieve()
                                            .bodyToMono(String.class);

        return logValuesMono.zipWith(messageMono,
                        (logValues, message) -> logValues + ": " + message)
                .onErrorReturn("Error");
    }


    private WebClient getRandomLoggingClient() {
        Random random = new Random();
        int randomIndex = random.nextInt(loggingWebClients.size());
        return loggingWebClients.get(randomIndex);
    }
}