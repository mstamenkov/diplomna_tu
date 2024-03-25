package com.example.backend.repository;

import com.example.backend.model.Entities;
import com.example.backend.serdes.EntitiesSerializer;
import com.example.backend.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class KafkaProducer implements ILifecycleController, IProducer {
    private org.apache.kafka.clients.producer.KafkaProducer<String, Entities> producer;

    @Override
    public void init() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, EntitiesSerializer.class.getName());
        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);
    }

    @Override
    public void destroy() {
        producer.close();
    }

    @Override
    public void send(Entities entity, String topic) throws JsonProcessingException {
        final Logger log = LoggerFactory.getLogger(CommandRepositoryImpl.class);
        ProducerRecord<String, Entities> record = new ProducerRecord<>(topic, entity.getSaveIdentification(), entity);
        sendRecord(log, record, false);
    }

    @Override
    public void delete(Entities entity, String topic) throws JsonProcessingException {
        final Logger log = LoggerFactory.getLogger(CommandRepositoryImpl.class);
        ProducerRecord<String, Entities> record = new ProducerRecord<>(topic, entity.getSaveIdentification(), null);
        sendRecord(log, record, true);
    }

    private void sendRecord(Logger log, ProducerRecord<String, Entities> record, boolean isDeleteOperation) {
        producer.send(record, (recordMetadata, e) -> {
            if (e == null) {
                log.info(String.format(Constants.LOG_MESSAGE,
                        isDeleteOperation ? "Delete operation" : "",
                        recordMetadata.topic(),
                        record.key(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp()));
            } else {
                log.error("Error while producing", e);
            }
        });
        producer.flush();
    }
}
