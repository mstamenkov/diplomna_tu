package com.example.backend.repository;

import com.example.backend.model.Command;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandRepositoryImplTest {
    private static final CommandRepository commandRepository = new TestCommandRepositoryImpl();
    private final String FIRST_COMMAND = "firstCommand";
    private final String SECOND_COMMAND = "secondCommand";
    private final String DESCRIPTION = "description of command";
    private final Map<String, String> INPUT_KEYS = Map.of("key1", "value1");
    private final Map<String, String> OUTPUT_KEYS = Map.of("result", "content");
    private final List<String> TAGS = List.of("command ");

    void createCommands() throws IOException {
        Command command = new Command(FIRST_COMMAND, DESCRIPTION, INPUT_KEYS, OUTPUT_KEYS, TAGS);
        Command command2 = new Command(SECOND_COMMAND, DESCRIPTION, INPUT_KEYS, OUTPUT_KEYS, TAGS);
        commandRepository.create(command);
        commandRepository.create(command2);
    }

    @Test
    void givenUnsupportedCommandName_whenGettingByName_thenCommandIsNotPresented() throws IOException {
        try {
            createCommands();
        }catch (Exception ignored){}
        assertThat(commandRepository.getByName("commandName")).isNotPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {FIRST_COMMAND, SECOND_COMMAND})
    void givenCommandNames_whenCreatingNewCommand_thenCommandIsPresented(String commandName) throws IOException {
        try {
            createCommands();
        }catch (Exception ignored){}
        Optional<Command> command = commandRepository.getByName(commandName);
        assertThat(command).isPresent();
        assertThat(command.get().getName()).isEqualTo(commandName);
        assertThat(command.get().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(command.get().getInputKeys()).isEqualTo(INPUT_KEYS);
        assertThat(command.get().getOutputKeys()).isEqualTo(OUTPUT_KEYS);
        assertThat(command.get().getTags()).isEqualTo(TAGS);
    }

    @Test
    void givenValidCommandId_whenGettingById_thenCommandIsPresented() throws IOException {
        try {
            createCommands();
        }catch (Exception ignored){}
        commandRepository.getAll().stream().map(Command::getId).forEach(id -> {
            assertThat(commandRepository.getById(id)).isPresent();
        });
    }

    @Test
    void givenMissingCommandId_whenGettingById_thenOptionalEmptyIsReturned() throws IOException {
        try {
            createCommands();
        }catch (Exception ignored){}
        assertThat(commandRepository.getById("-1")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-12", "34", "55"})
    void givenInvalidCommandId_whenGettingById_thenCommandIsEmpty(String id) {
        assertThat(commandRepository.getById(id)).isEmpty();
    }

    @Disabled
    @Test
    void givenWriteOnlyFolder_whenCreatingCommand_thenThrowException() {
        File folder = new File(new File("").getAbsolutePath());
        if (folder.setWritable(false)) {
            Throwable exception = assertThrows(IOException.class, () -> {
                commandRepository.create(new Command("commandName", null, null, null, null));
            });
            assertThat(exception.getMessage()).isEqualTo("Commands folder creation failed");
            folder.setWritable(true);
        }
    }
}
