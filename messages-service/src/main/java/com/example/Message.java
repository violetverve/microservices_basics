package com.example;

import java.util.UUID;

public class Message {
    public UUID id;
    public String txt;

    public Message(UUID id, String text) {
        this.id = id;
        this.txt = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", txt='" + txt + '\'' +
                '}';
    }
}
