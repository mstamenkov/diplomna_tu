package com.example.backend.service;

import com.example.backend.model.Command;
import com.example.backend.repository.CommandRepository;
import com.example.backend.repository.TestCommandRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandServiceTests {
    CommandRepository commandRepository = new TestCommandRepositoryImpl();
    CommandService commandService = new CommandService(commandRepository);

    void createCommand(String tag) throws IOException {
        Command command = new Command();
        command.setTags(List.of(tag));
        commandRepository.create(command);
    }

    @Test
    void givenNullTags_whenGettingAllCommand_thenAllCommandIsPresented() throws IOException {
        createCommand("");
        assertThat(commandService.getAllCommands(new ArrayList<>())).isEqualTo(commandRepository.getAll());
    }

    @Test
    void givenTagsList_whenGettingAllCommand_thenCommandsWithPresentedTagsIsReturned() throws IOException {
        createCommand("command");
        assertThat(commandService.getAllCommands(List.of("command"))).hasSize(1);
    }

    @Test
    void givenInvalidCommandId_whenGettingCommandById_thenExceptionIsThrown() {
        Throwable exception = assertThrows(NoSuchElementException.class, () -> commandService.getCommand(30));
        assertThat(exception.getMessage()).isEqualTo("Command with id 30 is not found.");
    }
}
