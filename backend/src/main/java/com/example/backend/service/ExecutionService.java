package com.example.backend.service;

import com.example.backend.dto.ExecutionParameters;
import com.example.backend.exception.ExecutableException;
import com.example.backend.model.Command;
import com.example.backend.model.ExecutableFactory;
import com.example.backend.model.Execution;
import com.example.backend.repository.CommandRepository;
import com.example.backend.repository.ExecutionRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.util.Constants.*;
import static com.example.backend.util.ExecutionStatus.*;
import static java.lang.String.format;

public class ExecutionService{
    CommandRepository commandRepository;
    ExecutionRepository executionRepository;

    private static final String INVALID_PARAM_TYPE_MSG = "parameter %s must to be %s";
    private static final String ILLEGAL_HTTP_METHOD_MSG = "Illegal HTTP method";
    private static final String EXECUTOR_NOT_FOUND_MSG = "Execution with id %s is not found.";
    private static final String INVALID_INPUT_KEY_MSG = "Invalid input key value";
    private static final String MISSING_KEY_MSG = "Invalid or missing key ";
    private static final String INVALID_EXPRESSION_MSG = "Invalid expression ";

    public ExecutionService(CommandRepository commandRepository, ExecutionRepository executionRepository) {
        this.commandRepository = commandRepository;
        this.executionRepository = executionRepository;
    }

    ExecutableFactory factory = new ExecutableFactory();

    public Execution executeCommand(ExecutionParameters params, Execution execution) throws Throwable {
        Command command = commandRepository.getByName(params.getCommandName()).orElseThrow(() -> new IllegalArgumentException(format("Command %s does not exists", params.getCommandName())));
        try {
            Map<String, Object> inputKey = params.getInputKeys().entrySet().stream().filter(entry -> command.getInputKeys().containsKey(entry.getKey())).map(entry -> validateParameterType(entry, command)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (params.getCommandName().equals(HTTP_REQUEST_COMMAND) || params.getCommandName().equals(OS_COMMAND)) {
                execution.setCurrentExecutor(params.getCommandName());
                createExecutionInstance(execution);
                execution.setOutputKeys(factory.create(params.getCommandName()).execute(inputKey));
            } else {
                params.setInputKeys(inputKey);
                execution.setOutputKeys(executeComposedCommand(params, execution));
            }

        } catch (ExecutableException e) {
            e.printStackTrace();
            execution.setStatus(FAILED);
            execution.setError(e.getMessage());
            if (execution.getError().contains(":")) {
                execution.setError(execution.getError().substring(execution.getError().indexOf(':') + 1));
            }
            execution.setTags(command.getTags());
            execution.setCommandName(params.getCommandName());
            executionRepository.create(execution);
            return execution;
        }

        //execution.setCommandName(params.getCommandName());
        if (params.getTags() != null) {
            List<String> allTags = params.getTags();
            allTags.addAll(command.getTags());
            execution.setTags(allTags);
        } else {
            execution.setTags(command.getTags());
        }
        execution.setStatus(FINISHED);
        execution.setCurrentExecutor(null);
        //executionRepository.create(execution);
        createExecutionInstance(execution);
        return execution;
    }

    private Map.Entry<String, Object> validateParameterType(Map.Entry<String, Object> inputKey, Command commandContract) {
        String keyType = commandContract.getInputKeys().get(inputKey.getKey());
        try {
            if (inputKey.getValue().getClass().getSimpleName().equals(keyType) || keyType.equals("Object")) {
                return inputKey;
            } else
                throw new IllegalArgumentException(format(INVALID_PARAM_TYPE_MSG, inputKey.getKey(), keyType));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(ILLEGAL_HTTP_METHOD_MSG);
        }
    }

    public Execution getExecution(String id) throws NoSuchElementException {
        return executionRepository.getById(id).orElseThrow(() ->
                new NoSuchElementException(format(EXECUTOR_NOT_FOUND_MSG, id)));
    }

    public List<Execution> getAllExecutions(List<String> tags) {
        List<Execution> executions = executionRepository.getAll();
        if (!tags.isEmpty()) {
            return executions.stream().filter(execution -> execution.getTags().containsAll(tags)).collect(Collectors.toList());
        } else return executions;
    }

    private Map<String, Object> executeComposedCommand(ExecutionParameters params, Execution currentExecutor) {
        Command commandSignature = commandRepository.getByName(params.getCommandName()).get();
        Map<String, Object> outputAliasExecution = new HashMap<>();
        Map<String, Object> outputKeys = new HashMap<>(commandSignature.getOutputKeys());
        commandSignature.getExecutors().forEach(command -> {
            if(currentExecutor != null) {
                currentExecutor.setCurrentExecutor(command.getCommandId());
                executionRepository.create(currentExecutor);
            }
            Map<String, Object> resolvedCommandInput = new HashMap<>(command.getInput());
            command.getInput().forEach((key, value) -> {
                if (value.toString().startsWith("$")) {
                    buildInputKeys(value.toString(), params, outputAliasExecution, resolvedCommandInput, key);
                } else resolvedCommandInput.put(key, value);
            });

            Optional<Command> subCommand = commandRepository.getByName(command.getCommandId());
            subCommand.ifPresent(subCommandDetails -> {
                try {
                    if (subCommandDetails.getName().equals(HTTP_REQUEST_COMMAND) || subCommandDetails.getName().equals(OS_COMMAND)) {
                        outputAliasExecution.put(command.getAlias(), factory.create(subCommandDetails.getName()).execute(resolvedCommandInput));
                    } else {
                        ExecutionParameters subCommandParams = new ExecutionParameters(resolvedCommandInput, params.getTags(), command.getCommandId());
                        outputAliasExecution.put(command.getAlias(), executeComposedCommand(subCommandParams, null));
                    }
                } catch (NullPointerException e) {
                    throw new ExecutableException(INVALID_INPUT_KEY_MSG, e);
                }

            });
        });
        commandSignature.getOutputKeys().forEach((key, value) -> {
            String[] dataPath = validateComposedCommandExpression(value);
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(gson.toJson(outputAliasExecution), JsonObject.class);
            resolveKeys(object, dataPath, outputKeys, key);
        });
        return outputKeys;
    }

    private void buildInputKeys(String dataPathString, ExecutionParameters params, Map<String, Object> outputAliasExecution, Map<String, Object> commandInput, String keyName) {
        JsonObject object;
        Gson gson = new Gson();
        String[] dataPath = validateComposedCommandExpression(dataPathString);
        if (dataPath[0].equals(INPUT)) {
            dataPath = Arrays.stream(dataPath).filter(elem -> !elem.equals(INPUT)).toArray(String[]::new);
            object = gson.toJsonTree(params.getInputKeys()).getAsJsonObject();
            //gson.toJsonTree(params.getInputKeys()).getAsJsonObject();
        } else {
            String executedCommandPrefix = dataPath[0];
            dataPath = Arrays.stream(dataPath).filter(elem -> !elem.equals(executedCommandPrefix)).toArray(String[]::new);
            object = gson.fromJson(gson.toJson(outputAliasExecution.get(executedCommandPrefix)), JsonObject.class);
        }
        resolveKeys(object, dataPath, commandInput, keyName);
    }

    private void resolveKeys(JsonObject object, String[] dataPath, Map<String, Object> commandKey, String keyName) {
        Gson gson = new Gson();
        if (!object.has(dataPath[0])) {
            throw new IllegalArgumentException(MISSING_KEY_MSG + Arrays.toString(dataPath));
        }
        for (int i = 0; i < dataPath.length; i++) {
            JsonElement temp;
            if (!object.has(dataPath[i])) {
                commandKey.replace(keyName, null);
                return;
            }
            if (object.get(dataPath[i]).isJsonObject()) {
                if (dataPath.length - 1 == i) {
                    temp = object.getAsJsonObject(dataPath[i]);
                    commandKey.replace(keyName, gson.fromJson(temp.getAsJsonObject(), LinkedHashMap.class));
                } else {
                    object = object.getAsJsonObject(dataPath[i]);
                }
            } else if (object.get(dataPath[i]).isJsonPrimitive()) {
                JsonPrimitive tempPrimitive = object.get(dataPath[i]).getAsJsonPrimitive();
                if(tempPrimitive.isNumber()){
                    commandKey.replace(keyName, tempPrimitive.getAsInt());
                } else {
                    commandKey.replace(keyName, tempPrimitive.getAsString());
                }
            } else if (object.get(dataPath[i]).isJsonArray()) {
                temp = object.getAsJsonArray(dataPath[i]);
                Object[] arr = gson.fromJson(temp, Object[].class);
                commandKey.replace(keyName, arr[Integer.parseInt(dataPath[++i])]);
                i++;
            }
        }
    }

    private String[] validateComposedCommandExpression(String expression) {
        if (!expression.matches("^\\$\\([\\w.]*[^.]+[)]$")) {
            throw new IllegalArgumentException(INVALID_EXPRESSION_MSG + expression);
        }
        return expression.replaceAll("[$()]", "").split("[.\\[\\]]+");
    }

    private Execution createExecutionInstance(Execution execution){
        executionRepository.create(execution);
        return execution;
    }

    public Execution initCommandExecution(ExecutionParameters params) throws Throwable {
        Execution execution = new Execution();
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    executeCommand(params, execution);
                } catch (Throwable e) {
                    e.printStackTrace();
                    execution.setStatus(FAILED);
                    execution.setError(e.getMessage());
                    execution.setTags(params.getTags());
                    if (execution.getError().contains(":")) {
                        execution.setError(execution.getError().substring(execution.getError().indexOf(':') + 1));
                    }
                    createExecutionInstance(execution);
                }
            }
        };
        thread.start();
        execution.setStatus(RUNNING);
        execution.setCommandName(params.getCommandName());
        execution.setInputKeys(params.getInputKeys());
        return execution;
    }
}
