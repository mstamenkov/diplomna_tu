/*
package com.example.backend.repository;

import com.example.backend.model.Execution;
import com.example.backend.util.ExecutionStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutionRepositoryImplTest {
    private static final ExecutionRepository executionRepository = new ExecutionRepositoryImpl();
    private final String NAME = "ExecName";
    private final ExecutionStatus STATUS = ExecutionStatus.FINISHED;
    private final Map<String, Object> INPUT_KEYS = Map.of("key1", "value1");
    private final Map<String, Object> OUTPUT_KEYS = Map.of("key2", "value2");
    private final List<String> TAGS = List.of("tag1", "tag2");

    Execution createExecution() {
        Execution execution = new Execution(NAME, STATUS, null, INPUT_KEYS, OUTPUT_KEYS, TAGS);
        executionRepository.create(execution);
        return execution;
    }

    @AfterAll
    static void deleteExecutions(){
        File executions = new File("executionData/");
        if(executions.exists()) {
            for (File execution : executions.listFiles()) {
                execution.delete();
            }
        }
    }

    @Test
    void givenExecutionId_whenGettingExecution_thenExecutionIsPresent() {
        String executionId = createExecution().getId();
        Optional<Execution> executionOptional = executionRepository.getById(executionId);
        assertThat(executionOptional).isPresent();
        Execution execution = executionOptional.get();
        assertThat(execution.getCommandName()).isEqualTo(NAME);
        assertThat(execution.getStatus().toString()).isEqualTo(STATUS.toString());
        assertThat(execution.getError()).isNull();
        assertThat(execution.getInputKeys()).isEqualTo(INPUT_KEYS);
        assertThat(execution.getOutputKeys()).isEqualTo(OUTPUT_KEYS);
        assertThat(execution.getTags()).isEqualTo(TAGS);
    }

    @Test
    void givenWriteOnlyFolder_whenCreatingCommand_thenThrowException() {
        File folder = new File(new File("").getAbsolutePath());
        if (folder.setWritable(false)) {
            Throwable exception = assertThrows(IOException.class, () -> {
                executionRepository.create(new Execution("commandName", null, null, null, null, null));
            });
            assertThat(exception.getMessage()).isEqualTo("Executions folder creation failed");
            folder.setWritable(true);
        }
    }

    @Test
    void whenGettingAllExecutions_thenAllExecutionsArePresent() {
        Execution execution = createExecution();
        executionRepository.create(new Execution());
        List<Execution> executions = executionRepository.getAll();
        assertThat(executions).hasSize(2);
        assertThat(executions).contains(execution);
    }
}
*/
