package com.example.backend.repository;

import org.apache.kafka.streams.state.KeyValueIterator;

import java.util.Optional;

public interface IStore<T> {
    Optional<T> get(String key);

    KeyValueIterator<String, T> getAll();
}
