package com.example.backend.repository;

import com.example.backend.model.Execution;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.backend.util.Constants.EXECUTIONS_TOPIC;
import static com.example.backend.util.Utils.saveToFile;

public class ExecutionRepositoryImpl implements ExecutionRepository {
    private static final String FILE_PATH = "executionData/";
    private final ExecutionsStore executionsStore;
    private final KafkaProducer producer;

    public ExecutionRepositoryImpl(KafkaProducer producer, ExecutionsStore store) {
        executionsStore = store;
        this.producer = producer;
    }

    public void create(Execution execution) {
        try {
            producer.send(execution, EXECUTIONS_TOPIC);
            saveToFile(execution, FILE_PATH);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save to file", e);
        }
    }

    public Optional<Execution> getById(String id) {
        return executionsStore.get(id);
    }

    public List<Execution> getAll() {
        return executionsStore.getAllAsStream().map(execution -> execution.value).collect(Collectors.toList());
    }

    /*private void loadExecutions() throws ClassNotFoundException {
        final File folder = new File(FILE_PATH);
        if (folder.listFiles() != null && executions.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            for (File file : folder.listFiles()) {
                try {
                    executions.add(objectMapper.readValue(file, Execution.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}
