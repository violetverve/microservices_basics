package com.example;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RestController
public class FacadeController {
    WebClient loggingWebClient = WebClient.create("http://localhost:8081");
    WebClient messagesWebClient = WebClient.create("http://localhost:8082");

    @GetMapping("/facade_service")
    public String fetchFromClients() {

        var messageValues = loggingWebClient.get()
                                        .uri("/log")
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .block();

        var message = messagesWebClient.get()
                                        .uri("/message")
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .block();

        return messageValues + ": " + message;
    }

    @PostMapping("/facade_service")
    public void sendToLoggingService(@RequestBody PayloadText text) {
        var msg = new Message(UUID.randomUUID(), text.txt);
        loggingWebClient.post()
                        .uri("/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(msg), Message.class)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
    }


}