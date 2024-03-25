package com.example.backend.dto;

import com.example.backend.model.Execution;
import com.example.backend.util.ExecutionStatus;

import java.util.List;
import java.util.Map;

public class ExecutionResult {
    private String id;
    private String commandName;
    private ExecutionStatus status;
    private String currentExecutor;
    private String error;
    private Map<String, Object> inputKeys;
    private Map<String, Object> outputKeys;
    private List<String> tags;

    public ExecutionResult(Execution execution){
        commandName = execution.getCommandName();
        id = execution.getId();
        status = execution.getStatus();
        error = execution.getError();
        inputKeys = execution.getInputKeys();
        outputKeys = execution.getOutputKeys();
        tags = execution.getTags();
        currentExecutor = execution.getCurrentExecutor();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, Object> getInputKeys() {
        return inputKeys;
    }

    public void setInputKeys(Map<String, Object> inputKeys) {
        this.inputKeys = inputKeys;
    }

    public Map<String, Object> getOutputKeys() {
        return outputKeys;
    }

    public void setOutputKeys(Map<String, Object> outputKeys) {
        this.outputKeys = outputKeys;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCurrentExecutor() {
        return currentExecutor;
    }

    public void setCurrentExecutor(String currentExecutor) {
        this.currentExecutor = currentExecutor;
    }
}
