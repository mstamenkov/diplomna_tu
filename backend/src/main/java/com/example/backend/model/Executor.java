package com.example.backend.model;

import java.util.Map;

public class Executor {
    private String commandId;
    private String alias;
    private Map<String, Object> input;

    public Executor(String commandId, String alias, Map<String, Object> input) {
        this.commandId = commandId;
        this.alias = alias;
        this.input = input;
    }

    public Executor() {
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }
}
