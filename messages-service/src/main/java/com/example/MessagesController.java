package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {

    @GetMapping("/message")
    public String getMessage() {
        return "The messages service is not yet implemented.";
    }
}
