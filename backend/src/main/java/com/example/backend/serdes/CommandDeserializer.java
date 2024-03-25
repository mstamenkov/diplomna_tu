package com.example.backend.serdes;

import com.example.backend.model.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class CommandDeserializer implements Deserializer<Command> {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Command deserialize(String topic, byte[] data) {
        if(data == null)return null;
        try {
            return objectMapper.readValue(data, Command.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Command deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
