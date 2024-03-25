package com.example.backend.serdes;

import com.example.backend.model.Execution;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ExecutionSerdes implements Serde<Execution>{
    private final ExecutionSerializer serializer = new ExecutionSerializer();
    private final ExecutionDeserializer deserializer = new ExecutionDeserializer();

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
    public Serializer<Execution> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Execution> deserializer() {
        return deserializer;
    }
}
