package com.example.backend.model;

import com.example.backend.util.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

public class Execution implements Serializable, Entities {
    private final String id = UUID.randomUUID().toString();
    private String commandName;
    private ExecutionStatus status;
    private String error;
    private String currentExecutor;
    private Map<String, Object> inputKeys = new HashMap<>();
    private Map<String, Object> outputKeys = new HashMap<>();
    private List<String> tags;

    public Execution(ExecutionStatus status, String error, Map<String, Object> outputKeys) {
        this.status = status;
        this.error = error;
        this.outputKeys = outputKeys;
    }

    public Execution(String commandName, ExecutionStatus status, String error, Map<String, Object> inputKeys, Map<String, Object> outputKeys, List<String> tags) {
        this.commandName = commandName;
        this.status = status;
        this.error = error;
        this.inputKeys = inputKeys;
        this.outputKeys = outputKeys;
        this.tags = tags;
    }

    public Execution() {
    }

    public String getId() {
        return id;
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

    public void setError(String errorProperties) {
        this.error = errorProperties;
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

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCurrentExecutor() {
        return currentExecutor;
    }

    public void setCurrentExecutor(String currentExecutor) {
        this.currentExecutor = currentExecutor;
    }

    @JsonIgnore
    @Override
    public String getSaveIdentification() {
        return getId();
    }
}
