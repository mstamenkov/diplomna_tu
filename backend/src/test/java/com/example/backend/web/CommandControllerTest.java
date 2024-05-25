package com.example.backend.web;

import com.example.backend.repository.TestCommandRepositoryImpl;
import com.example.backend.model.Command;
import com.example.backend.repository.CommandRepository;
import com.example.backend.service.CommandService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandControllerTest {
    CommandRepository commandRepository = new TestCommandRepositoryImpl();
    CommandService commandService = new CommandService(commandRepository);
    CommandController controller = new CommandController(commandService);

    String createCommand(String tag) throws IOException {
        Command command = new Command();
        command.setTags(List.of(tag));
        commandRepository.create(command);
        return command.getId();
    }

    @Test
    void givenValidCommandId_whenGettingById_ThenCommandIsReturned() throws IOException {
        String id = createCommand("");
        ResponseEntity entity = controller.getCommand(id);
        Command command = (Command) entity.getBody();
        assertThat(command.getId()).isEqualTo(id);
        assertThat(entity.getStatusCodeValue()).isEqualTo(200);

    }

    @Test
    void givenInvalidCommandId_whenGettingById_ThenExceptionIsThrown(){
        Throwable exception = assertThrows(NoSuchElementException.class, () -> controller.getCommand("10"));
        assertThat(exception.getMessage()).isEqualTo("Command with id 10 is not found.");
    }

    @Test
    void whenGettingAllCommandsWithoutTags_thenAllCommandIsPresented() throws IOException {
        createCommand("tag");
        createCommand("tag1");
        assertThat(controller.getAllCommands(null).getBody()).asList().hasSize(2);
    }

    @Test
    void whenGettingAllCommandsWithTags_thenCommandIsPresented() throws IOException {
        String id = createCommand("new tag");
        List<Command> commands = (List<Command>) controller.getAllCommands(List.of("new tag")).getBody();
        assertThat(commands.get(0).getId()).isEqualTo(id);
    }
}
