package com.example.backend.util;

import com.example.backend.model.Execution;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.example.backend.util.Utils.saveToFile;
import static com.example.backend.util.Utils.toJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilsTests {
    private final String FILE_PATH = "./";

    @Test
    void givenEntitiesObject_whenCreatingJsonStringByObject_thenStringIsReturned() throws JsonProcessingException {
        Execution execution = new Execution(ExecutionStatus.FINISHED, "some error message", Map.of("key1", "value1"));
        assertThat(toJsonString(execution)).contains("\"commandName\":null,\"status\":\"FINISHED\",\"error\":\"some error message\",\"inputKeys\":{},\"outputKeys\":{\"key1\":\"value1\"},\"tags\":null");
    }

    @Test
    void givenEntitiesObject_whenSavingToFile_thenFileIsCreated() throws IOException {
        Execution execution = new Execution(ExecutionStatus.FINISHED, "some error message", Map.of("key1", "value1"));
        saveToFile(execution, FILE_PATH);
        File file = new File(FILE_PATH + execution.getSaveIdentification());
        assertThat(file).exists();
        file.delete();
    }

    @Test
    void givenWriteOnlyFolder_whenSavingToFile_thenExceptionIsThrown() {
        File folder = new File(new File("").getAbsolutePath());
        if (folder.setWritable(false)) {
            Throwable exception = exception = assertThrows(IOException.class, () -> saveToFile(new Execution(), FILE_PATH));
            assertThat(exception.getMessage()).isEqualTo("Executions folder creation failed");
            folder.setWritable(true);
        }
    }
}
