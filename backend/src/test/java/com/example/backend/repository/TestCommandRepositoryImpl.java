package com.example.backend.repository;

import com.example.backend.model.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestCommandRepositoryImpl implements CommandRepository {
    private final List<Command> commands = new ArrayList<>();

    @Override
    public void create(Command command) {
        commands.add(command);
    }

    @Override
    public Command update(Command command) throws IOException {
        return null;
    }

    @Override
    public void delete(String id) throws IOException {

    }

    @Override
    public Optional<Command> getById(String id) {
        for (Command command : commands) {
            if (command.getId().equals(id)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Command> getByName(String name) {
        for (Command command : commands) {
            if (command.getName().equals(name)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Command> getAll() {
        return commands;
    }
}
