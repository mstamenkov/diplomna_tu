package com.example.backend.web;

import com.example.backend.dto.ExecutionResult;
import com.example.backend.repository.TestCommandRepositoryImpl;
import com.example.backend.repository.TestExecutionRepositoryImpl;
import com.example.backend.dto.ExecutionParameters;
import com.example.backend.model.Command;
import com.example.backend.model.Execution;
import com.example.backend.repository.CommandRepository;
import com.example.backend.repository.ExecutionRepository;
import com.example.backend.service.ExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static com.example.backend.util.Constants.*;
import static com.example.backend.util.Constants.BODY;
import static com.example.backend.util.ExecutionStatus.FINISHED;
import static com.example.backend.util.ExecutionStatus.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutionControllerTest {
    ExecutionRepository executionRepository = new TestExecutionRepositoryImpl();
    CommandRepository commandRepository = new TestCommandRepositoryImpl();
    ExecutionService executionService = new ExecutionService(commandRepository, executionRepository);
    ExecutionController controller = new ExecutionController(executionService);

    private static final Map<String, Object> headers = new LinkedHashMap<>();
    private static final Map<String, Object> body = new HashMap<>();
    private static final Map<String, Object> inputKeys = new HashMap<>();

    void createHttpRequest() {
        headers.clear();
        body.clear();
        inputKeys.clear();
        headers.put("Authentication", "Basic aW1lOnBhcm9sYQ==");
        headers.put("testHeader", "headers");
        headers.put("testArray", List.of("1", "fdg", "554"));
        body.put("local", 1);
        body.put("test2", "test2");
        body.put("list", List.of("gfd", "fdf"));
        inputKeys.put(URL, "https://postman-echo.com/post");
        inputKeys.put(HTTP_METHOD, "POST");
        inputKeys.put(HEADERS, headers);
        inputKeys.put(BODY, body);
    }

    Execution createExecution(List<String> tags) {
        Execution execution = new Execution();
        execution.setTags(tags);
        execution.setStatus(FINISHED);
        executionRepository.create(execution);
        return execution;
    }

    @Test
    void givenValidExecutionId_whenGettingById_ThenExecutionIsReturned(){
        String id = createExecution(List.of("tag")).getId();
        ResponseEntity entity = controller.getExecution(id);
        Execution execution = (Execution) entity.getBody();
        assertThat(execution.getStatus()).isEqualTo(FINISHED);
        assertThat(entity.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void givenInvalidExecutionId_whenGettingById_ThenExceptionIsThrown(){
        Throwable exception = assertThrows(NoSuchElementException.class, () -> controller.getExecution("invalidId"));
        assertThat(exception.getMessage()).isEqualTo("Execution with id invalidId is not found.");
    }

    @Test
    void whenGettingAllExecutionsWithoutTags_thenAllExecutionsIsPresented(){
        createExecution(List.of("tag"));
        createExecution(List.of("tag"));
        assertThat(controller.getAllExecutions(null).getBody()).asList().hasSize(2);
    }

    @Test
    void whenGettingAllExecutionsWithTags_thenExecutionIsPresented(){
        String id = createExecution(List.of("new tag")).getId();
        List<Execution> executions = (List<Execution>) controller.getAllExecutions(List.of("new tag")).getBody();
        assertThat(executions.get(0).getId()).isEqualTo(id);
    }

    @Test
    void givenExecutionParams_whenExecutingCommand_thenExecutionIsRunning() throws Throwable {
        createHttpRequest();
        Command command = new Command("HttpRequestCommand", "executes HttpRequest by given input keys.", new HashMap<>(), new HashMap<>(), new ArrayList<>());
        command.getInputKeys().put(URL, "String");
        command.getInputKeys().put(BODY, "Object");
        command.getInputKeys().put(HTTP_METHOD, "String");
        command.getInputKeys().put(TIMEOUT, "Integer");
        command.getInputKeys().put(HEADERS, "Object");
        command.getTags().add("httpRequest");
        commandRepository.create(command);
        ExecutionParameters params = new ExecutionParameters();
        params.setCommandName("HttpRequestCommand");
        params.setInputKeys(inputKeys);
        List<String> list = new ArrayList<>();
        list.add("");
        params.setTags(list);
        ExecutionResult executionResult = controller.execute(params);
        assertThat(executionResult.getCommandName()).isEqualTo(command.getName());
        assertThat(executionResult.getInputKeys()).isEqualTo(inputKeys);
        assertThat(executionResult.getStatus()).isEqualTo(RUNNING);
        assertThat(executionResult.getError()).isNull();
        assertThat(executionResult.getOutputKeys().get(RESPONSE_CODE)).isNull();

    }
}
