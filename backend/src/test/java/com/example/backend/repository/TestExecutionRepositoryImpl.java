package com.example.backend.repository;

import com.example.backend.model.Execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestExecutionRepositoryImpl implements ExecutionRepository {
    private final List<Execution> executions = new ArrayList<>();

    @Override
    public void create(Execution execution) {
        executions.add(execution);
    }

    @Override
    public Optional<Execution> getById(String id) {
        for (Execution execution : executions) {
            if (execution.getId().equals(id)) {
                return Optional.of(execution);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Execution> getAll() {
        return executions;
    }

    public void clear() {
        executions.clear();
    }
}
