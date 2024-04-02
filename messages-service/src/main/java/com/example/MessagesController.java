package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MessagesController {

     private final MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @GetMapping("/message")
    public List<String> getMessage() {
        List<Message> messageList = messagesService.getMessageList();

        return messageList.stream()
                .map(message -> message.txt)
                .collect(Collectors.toList());
    }
}
