package com.example.backend.repository;

import com.example.backend.model.Command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CommandRepository {
    void create(Command command) throws IOException;

    Command update(Command command) throws IOException;

    void delete(String id) throws IOException;

    Optional<Command> getById(String id);

    Optional<Command> getByName(String name);

    List<Command> getAll();
}
