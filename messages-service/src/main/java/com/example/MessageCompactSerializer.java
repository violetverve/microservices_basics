package com.example;

import java.util.UUID;

import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;
import jakarta.annotation.Nonnull;

public class MessageCompactSerializer implements CompactSerializer<Message> {
    @Nonnull @Override
    public Message read(CompactReader reader) {
        String idAsString = reader.readString("id");
        if (idAsString == null) {
            throw new NullPointerException("ID string must not be null");
        }

        UUID id = UUID.fromString(idAsString);
        String text = reader.readString("txt");
        return new Message(id, text);
    }

    @Nonnull @Override
    public String getTypeName() {
        return "Message";
    }

    @Override
    public void write(CompactWriter writer, Message message) {
        writer.writeString("id", message.id.toString());
        writer.writeString("txt", message.txt);
    }

    @Nonnull @Override
    public Class<Message> getCompactClass() {
        return Message.class;
    }
}
