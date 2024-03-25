package com.example.backend.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionParameters {
    private Map<String, Object> inputKeys = new HashMap<>();
    private List<String> tags;
    private String commandName;

    public ExecutionParameters(Map<String, Object> inputKeys, List<String> tags, String commandName) {
        this.inputKeys = inputKeys;
        this.tags = tags;
        this.commandName = commandName;
    }

    public ExecutionParameters() {
    }

    public Map<String, Object> getInputKeys() {
        return inputKeys;
    }

    public void setInputKeys(Map<String, Object> inputKeys) {
        this.inputKeys = inputKeys;
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
}
