package com.example.backend.repository;

import com.example.backend.model.Execution;

import java.util.List;
import java.util.Optional;

public interface ExecutionRepository {
    void create(Execution execution);

    Optional<Execution> getById(String id);

    List<Execution> getAll();

}
