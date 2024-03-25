package com.example.backend.repository;

import com.example.backend.model.Entities;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IProducer {
    void send(Entities entity, String topic) throws JsonProcessingException;
    void delete(Entities entity, String topic) throws JsonProcessingException;
}
