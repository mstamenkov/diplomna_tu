package com.example.backend.model;

import static com.example.backend.util.Constants.HTTP_REQUEST_COMMAND;
import static com.example.backend.util.Constants.OS_COMMAND;
import static java.lang.String.format;

public class ExecutableFactory implements IExecutableFactory {

    @Override
    public Executable create(String commandName) {
        Executable execution;
        if (commandName.equals(HTTP_REQUEST_COMMAND)) {
            execution = new HttpRequestExecutable();
        } else if (commandName.equals(OS_COMMAND)) {
            execution = new OSExecutable();
        } else throw new IllegalArgumentException(format("%s is not a valid executable",commandName));
        return execution;
    }
}
