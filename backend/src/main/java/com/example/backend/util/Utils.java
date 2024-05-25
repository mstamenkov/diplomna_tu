package com.example.backend.util;

import com.example.backend.model.Entities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static java.lang.String.format;
import static org.apache.kafka.streams.StreamsConfig.*;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

public class Utils {
    public static String toJsonString(Object json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(json);
    }

    public static void saveToFile(Entities entity, String filePath) throws IOException {
        File folder = new File(filePath);
        if (!folder.exists() && !folder.mkdir()) {
            throw new IOException(format("%s folder creation failed", entity.getClass().getSimpleName()));
        }
        try (FileWriter fw = new FileWriter(format("%s%s", filePath, entity.getSaveIdentification()));
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(toJsonString(entity));
        }
    }

    public static Properties buildStreamProps(String id, Class serdesClass){
        Properties properties = new Properties();
        properties.put(APPLICATION_ID_CONFIG, id);
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9093");
        properties.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, serdesClass);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return properties;
    }

    public static void waitStreamToBecomeReady(KafkaStreams stream) {
        while (!stream.state().equals(KafkaStreams.State.RUNNING)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
