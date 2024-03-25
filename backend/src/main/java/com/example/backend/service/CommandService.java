package com.example.backend.service;

import com.example.backend.model.Command;
import com.example.backend.model.Executor;
import com.example.backend.repository.CommandRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.util.Constants.HTTP_REQUEST_COMMAND;
import static com.example.backend.util.Constants.OS_COMMAND;
import static java.lang.String.format;

public class CommandService {

    CommandRepository commandRepository;

    private static final String COMMAND_NOT_FOUND_MSG = "Command with id %s is not found.";
    private static final String COMMAND_INVOKE_MSG = "Command cannot invoke itself";

    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public void commandInit() throws IOException { //For test purposes
        Command command = new Command("HttpRequestCommand", "executes HttpRequest by given input keys.", new HashMap<>(), new HashMap<>(), new ArrayList<>());
        command.getInputKeys().put("url", "String");
        command.getInputKeys().put("body", "Object");
        command.getInputKeys().put("method", "String");
        command.getInputKeys().put("timeout", "Integer");
        command.getInputKeys().put("headers", "Object");
        command.getOutputKeys().put("responseCode", "Integer");
        command.getOutputKeys().put("result", "Object");
        command.getTags().add("httpRequest");

        Command command2 = new Command("OSCommand", "executes OS command", new HashMap<>(), new HashMap<>(), new ArrayList<>());
        command2.getInputKeys().put("command", "String");
        command2.getOutputKeys().put("status", "Integer");
        command2.getOutputKeys().put("result", "String");
        command2.getTags().add("os");
        command2.getTags().add("command");

        commandRepository.create(command);
        commandRepository.create(command2);
    }

    public Command getCommand(long id) throws NoSuchElementException {
        return commandRepository.getById(id).orElseThrow(() ->
                new NoSuchElementException(format(COMMAND_NOT_FOUND_MSG, id)));
    }

    public List<Command> getAllCommands(List<String> tags) {
        List<Command> commands = commandRepository.getAll();
        if (!tags.isEmpty()) {
            return commands.stream().filter(command -> command.getTags().containsAll(tags)).collect(Collectors.toList());
        } else return commands;
    }

    public void createCommand(Command command) throws IOException {
        if(command.getName() == null || command.getInputKeys() == null || command.getOutputKeys() == null)throw new IllegalArgumentException("params can't be empty");
        if(command.getExecutors() != null) {
            command.getExecutors().forEach(executor -> {
                if (executor.getCommandId().equals(command.getName()))
                    throw new IllegalArgumentException(COMMAND_INVOKE_MSG);
                validateCommand(executor, command.getName());
            });
        }
        commandRepository.create(command);
    }

    private void validateCommand(Executor executor, String commandName){
        if(!executor.getCommandId().equals(HTTP_REQUEST_COMMAND) && !executor.getCommandId().equals(OS_COMMAND)){
            Optional<Command> command = commandRepository.getByName(executor.getCommandId());
            if(command.isEmpty()) throw new NoSuchElementException(format(COMMAND_NOT_FOUND_MSG, executor.getCommandId()));
            if(command.get().getExecutors().stream().anyMatch(com -> com.getCommandId().equals(commandName) || commandRepository.getByName(executor.getCommandId()).get().getExecutors().stream().filter(exec -> !exec.getCommandId().equals(HTTP_REQUEST_COMMAND) && !exec.getCommandId().equals(OS_COMMAND)).anyMatch(subcom -> com.getCommandId().equals(subcom.getCommandId())))){
                throw new IllegalArgumentException(COMMAND_INVOKE_MSG);
            }else {
                command.get().getExecutors().forEach(exec -> {
                    validateCommand(exec, commandName);
                });
            }
        }
    }

    public Command editCommand(Command command) throws IOException {
        if(command.getExecutors() != null) {
            command.getExecutors().forEach(executor -> {
                if (executor.getCommandId().equals(command.getName()))
                    throw new IllegalArgumentException(COMMAND_INVOKE_MSG);
                validateCommand(executor, command.getName());
            });
        }
        return commandRepository.update(command);
    }

    public Command deleteCommand(long id) throws IOException {
        return commandRepository.delete(id);
    }
}
