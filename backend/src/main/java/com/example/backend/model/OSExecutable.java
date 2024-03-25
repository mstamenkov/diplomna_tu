package com.example.backend.model;

import com.example.backend.exception.ExecutableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static com.example.backend.util.Constants.*;

public class OSExecutable implements Executable {
    private final ProcessBuilder processBuilder;

    public OSExecutable(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    public OSExecutable() {
        processBuilder = new ProcessBuilder();
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputParams) {
        HashMap<String, Object> outputKeys = new HashMap<>();
        final String commandLine = (String) inputParams.get(COMMAND);
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            processBuilder.command("cmd.exe", "/c", commandLine);
        } else {
            processBuilder.command("sh", "-c", commandLine);
        }

        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                output.append(buffer + '\n');
            }
            final int exitCode = process.waitFor();
            outputKeys.put(EXIT_CODE, exitCode);

            if (exitCode == 0) {
                System.out.println(output);
                outputKeys.put(OUTPUT, output);
            } else {
                buildError(process);
            }
            return outputKeys;
        } catch (IOException | InterruptedException e) {
            throw new ExecutableException(e.getMessage(), new Exception());
        }
    }

    private void buildError(Process process) throws IOException {
        String buffer;
        StringBuilder output = new StringBuilder();
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
        while ((buffer = errorReader.readLine()) != null) {
            output.append(buffer + '\n');
        }
        throw new IOException(String.valueOf(output));
    }
}
