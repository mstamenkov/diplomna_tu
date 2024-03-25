package com.example.backend.repository;

import com.example.backend.model.Command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CommandRepository {
    void create(Command command) throws IOException;

    Command update(Command command) throws IOException;

    Command delete(long id) throws IOException;

    Optional<Command> getById(long id);

    Optional<Command> getByName(String name);

    List<Command> getAll();
}
