package com.example.backend.serdes;

import com.example.backend.model.Command;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CommandSerdes implements Serde<Command>{
    private final CommandSerializer serializer = new CommandSerializer();
    private final CommandDeserializer deserializer = new CommandDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        deserializer.close();
        serializer.close();
    }

    @Override
    public Serializer<Command> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Command> deserializer() {
        return deserializer;
    }
}
