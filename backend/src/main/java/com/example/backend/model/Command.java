package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Command implements Serializable, Entities {
    private static final AtomicInteger count = new AtomicInteger(0);
    private long id;
    private String name;
    private String description;
    private Map<String, String> inputKeys;
    private Map<String, String> outputKeys;
    private List<String> tags;
    private List<Executor> executors;

    public Command(String name, String description, Map<String, String> inputKeys, Map<String, String> outputKeys, List<String> tags) {
        this.name = name;
        this.description = description;
        this.inputKeys = inputKeys;
        this.outputKeys = outputKeys;
        this.tags = tags;
        id = count.incrementAndGet();
    }

    public Command(String name, String description, Map<String, String> inputKeys, Map<String, String> outputKeys, List<String> tags, List<Executor> executors) {
        this.name = name;
        this.description = description;
        this.inputKeys = inputKeys;
        this.outputKeys = outputKeys;
        this.tags = tags;
        this.executors = executors;
        id = count.incrementAndGet();
    }

    public Command() {
        id = count.incrementAndGet();
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getInputKeys() {
        return inputKeys;
    }

    public void setInputKeys(Map<String, String> inputKeys) {
        this.inputKeys = inputKeys;
    }

    public Map<String, String> getOutputKeys() {
        return outputKeys;
    }

    public void setOutputKeys(Map<String, String> outputKeys) {
        this.outputKeys = outputKeys;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Executor> getExecutors() {
        return executors;
    }

    public void setExecutors(List<Executor> executors) {
        this.executors = executors;
    }

    @Override
    @JsonIgnore
    public String getSaveIdentification() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command)) return false;
        Command command = (Command) o;
        return Objects.equals(getName(), command.getName()) && Objects.equals(getDescription(), command.getDescription()) && Objects.equals(getInputKeys(), command.getInputKeys()) && Objects.equals(getOutputKeys(), command.getOutputKeys()) && Objects.equals(getTags(), command.getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getInputKeys(), getOutputKeys(), getTags());
    }
}
