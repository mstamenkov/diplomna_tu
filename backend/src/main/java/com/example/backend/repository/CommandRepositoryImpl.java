package com.example.backend.repository;

import com.example.backend.exception.ForbiddenOperationException;
import com.example.backend.model.Command;
import org.apache.kafka.common.errors.KafkaStorageException;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.backend.util.Constants.*;
import static com.example.backend.util.Utils.saveToFile;
import static java.lang.String.format;

public class CommandRepositoryImpl implements CommandRepository {
    private static final String FILE_PATH = "commandData/";
    private final CommandsStore commandsStore;
    private final KafkaProducer producer;

    public CommandRepositoryImpl(KafkaProducer producer, CommandsStore store) {
        commandsStore = store;
        this.producer = producer;
    }

    public void create(Command command) throws IOException {
        if (commandsStore.get(command.getName()).isPresent())
            throw new FileAlreadyExistsException(format("Command %s with same name already exists", command.getName()));
        saveToFile(command, FILE_PATH);
        producer.send(command, COMMANDS_TOPIC);
    }

    public Command update(Command command) throws IOException {
        Optional<Command> oldCommand = getByName(command.getName());
        if (oldCommand.isPresent()) {
            command.setId(oldCommand.get().getId());
            saveToFile(command, FILE_PATH);
            producer.send(command, COMMANDS_TOPIC);
        } else throw new KafkaStorageException(format("Command with name %s does not exist", command.getName()));
        return command;
    }

    public void delete(String id) throws IOException {
        Optional<Command> command = getById(id);
        if (command.isPresent()) {
            String commandName = command.get().getName();
            if (commandName.equals(HTTP_REQUEST_COMMAND) || commandName.equals(OS_COMMAND))
                throw new ForbiddenOperationException("Provided commands cannot be deleted");
            producer.delete(command.get(), COMMANDS_TOPIC);
        } else throw new KafkaStorageException(format("Command with id %s does not exist", id));
    }

    public Optional<Command> getById(String id) {
        return commandsStore.getAllAsStream().map(collection -> collection.value).filter(command -> command.getId().equals(id)).findAny();
    }

    public Optional<Command> getByName(String name) {
        return commandsStore.get(name);
    }

    public List<Command> getAll() {
        return commandsStore.getAllAsStream().map(command -> command.value).collect(Collectors.toList());
    }

    /*private void loadCommands() {
        final File folder = new File(FILE_PATH);
        if (folder.listFiles() != null && commands.isEmpty()) {
            for (File file : folder.listFiles()) {
                try {
                    commands.add(objectMapper.readValue(file, Command.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}
