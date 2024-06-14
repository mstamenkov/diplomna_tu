package com.example.backend.service;

import com.example.backend.dto.ExecutionParameters;
import com.example.backend.model.Command;
import com.example.backend.model.Execution;
import com.example.backend.model.Executor;
import com.example.backend.repository.CommandRepository;
import com.example.backend.repository.TestCommandRepositoryImpl;
import com.example.backend.repository.TestExecutionRepositoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static com.example.backend.util.Constants.*;
import static com.example.backend.util.ExecutionStatus.FINISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutionServiceTests {
    TestExecutionRepositoryImpl executionRepository = new TestExecutionRepositoryImpl();
    static CommandRepository commandRepository = new TestCommandRepositoryImpl();
    ExecutionService executionService = new ExecutionService(commandRepository, executionRepository);

    private static final Map<String, Object> headers = new LinkedHashMap<>();
    private static final Map<String, Object> body = new LinkedHashMap<>();
    private static final Map<String, Object> inputKeys = new HashMap<>();
    private static final List<String> tags = new ArrayList<>();
    private static final Map<String, String> composedCommandInput = new HashMap<>();

    static void setInputKeys(){
        composedCommandInput.put(URL, "String");
        composedCommandInput.put(BODY, "Object");
        composedCommandInput.put(HTTP_METHOD, "String");
        composedCommandInput.put(TIMEOUT, "Integer");
        composedCommandInput.put(HEADERS, "Object");
        composedCommandInput.put(COMMAND, "String");
    }

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

    @BeforeAll
    static void createCommand() throws IOException {
        Command command = new Command("HttpRequestCommand", "executes HttpRequest by given input keys.", new HashMap<>(), new HashMap<>(), new ArrayList<>());
        command.getInputKeys().put(URL, "String");
        command.getInputKeys().put(BODY, "Object");
        command.getInputKeys().put(HTTP_METHOD, "String");
        command.getInputKeys().put(TIMEOUT, "Integer");
        command.getInputKeys().put(HEADERS, "Object");
        command.getTags().add("httpRequest");
        commandRepository.create(command);

        Command osCommand = new Command("OSCommand", "executes HttpRequest by given input keys.", new HashMap<>(), new HashMap<>(), new ArrayList<>());
        command.getInputKeys().put(COMMAND, "String");
        commandRepository.create(osCommand);

        setInputKeys();

        Command composedCommand = new Command("composedCommand", "fdf", composedCommandInput, Map.of("output1", "$(abc.output.body.arr[1])", "output2", "$(abc2.output2)"), new ArrayList<>(),
                List.of(new Executor("HttpRequestCommand", "abc", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}))), new Executor("composedCommand2", "abc2", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}), "command", "$(input.command)"))));

        Command composedCommand2 = new Command("composedCommand2", "fdf", composedCommandInput, Map.of("output1", "$(abc.output.body)", "output2", "$(abc23.output)"), new ArrayList<>(),
                List.of(new Executor("HttpRequestCommand", "abc", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}))), new Executor("OSCommand", "abc23", Map.of("command", "$(input.command)"))));

        commandRepository.create(composedCommand);
        commandRepository.create(composedCommand2);

    }

    @Test
    void givenValidInputData_whenExecutingHttpRequest_thenExecutionIsReturned() throws Throwable {
        createHttpRequest();
        ExecutionParameters params = new ExecutionParameters();
        params.setCommandName("HttpRequestCommand");
        params.setInputKeys(inputKeys);
        params.setTags(tags);
        Execution execution = executionService.executeCommand(params, new Execution());
        Map<String, Object> bodyResult = (Map<String, Object>) execution.getOutputKeys().get(OUTPUT);
        assertThat(bodyResult.get(BODY)).isEqualTo(body);
        assertThat(execution.getStatus()).isEqualTo(FINISHED);
        assertThat(execution.getError()).isNull();
    }

    @Test
    void givenWrongInputParamVariableType_whenExecutingHttpRequest_thenExceptionIsThrown() {
        createHttpRequest();
        ExecutionParameters params = new ExecutionParameters();
        inputKeys.put(TIMEOUT, " ");
        params.setCommandName("HttpRequestCommand");
        params.setInputKeys(inputKeys);
        params.setTags(List.of("tag"));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> executionService.executeCommand(params, new Execution()));
        assertThat(exception.getMessage()).isEqualTo("parameter timeout must to be Integer");
    }

    @Test
    void givenValidExecutionId_whenGettingExecutionById_thenExecutionIsPresented() throws Throwable {
        createHttpRequest();
        ExecutionParameters params = new ExecutionParameters();
        params.setCommandName("HttpRequestCommand");
        params.setInputKeys(inputKeys);
        params.setTags(tags);
        Execution execution = executionService.executeCommand(params, new Execution());
        assertThat(executionService.getExecution(execution.getId())).isEqualTo(execution);
    }

    @Test
    void givenInvalidExecutionId_whenGettingExecutionById_thenExceptionIsThrown() {
        Throwable exception = assertThrows(NoSuchElementException.class, () -> executionService.getExecution("wrongId"));
        assertThat(exception.getMessage()).isEqualTo("Execution with id wrongId is not found.");
    }

    @Test
    void givenNullTags_whenGettingAllExecutions_thenAllExecutionsIsPresented() {
        assertThat(executionService.getAllExecutions(new ArrayList<>())).isEqualTo(executionRepository.getAll());
    }

    @Test
    void givenTagsList_whenGettingAllExecutions_thenExecutionsWithPresentedTagsIsReturned() throws Throwable {
        executionRepository.clear();
        tags.add("execution");
        createHttpRequest();
        ExecutionParameters params = new ExecutionParameters();
        params.setCommandName("HttpRequestCommand");
        params.setInputKeys(inputKeys);
        params.setTags(tags);
        Execution execution = executionService.executeCommand(params, new Execution());
        List<String> tag = new ArrayList<>();
        tag.add("test");
        params.setTags(tag);
        executionService.executeCommand(params, new Execution());
        assertThat(executionService.getAllExecutions(tags)).hasSize(2);
        assertThat(executionService.getAllExecutions(tags)).contains(execution);
    }

    //COMPOSED COMMANDS TESTS//

    @Test
    void givenValidInputData_whenComposedCommandIsExecuted_thenExecutionIsReturned() throws Throwable {
        ExecutionParameters executionParams = new ExecutionParameters();
        executionParams.setInputKeys(Map.of("url", "https://postman-echo.com/post", "timeout", 25, "headers", Map.of("Authentication", "Basic aW1lOnBhcm9sYQ=="), "method", "post", "command", "echo test echo message"));
        executionParams.setCommandName("composedCommand");
        Execution execution = executionService.executeCommand(executionParams, new Execution());
        assertThat(execution.getStatus()).isEqualTo(FINISHED);
        assertThat(execution.getError()).isNull();
        assertThat(execution.getOutputKeys().get("output1")).isEqualTo("gd");
        assertThat(execution.getOutputKeys().get("output2")).asString().contains("test echo message");
    }

    @Test
    void givenIllegalSubOutputKeys_whenComposedCommandIsCreated_thenNullValueWillBeReturned() throws Throwable {
        setInputKeys();
        Command composedCommand = new Command("composedCommand3", "fdf", composedCommandInput, Map.of("output1", "$(abc.result.body.arr[1])", "output2", "$(abc2.output2.illegal.path)"), new ArrayList<>(),
                List.of(new Executor("HttpRequestCommand", "abc", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}))), new Executor("composedCommand2", "abc2", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}), "command", "$(input.command)"))));
        commandRepository.create(composedCommand);

        ExecutionParameters executionParams = new ExecutionParameters();
        executionParams.setInputKeys(Map.of("url", "https://postman-echo.com/post", "timeout", 25, "headers", Map.of("Authentication", "Basic aW1lOnBhcm9sYQ=="), "method", "post", "command", "dir"));
        executionParams.setCommandName("composedCommand3");
        Execution execution = executionService.executeCommand(executionParams, new Execution());
        assertThat(execution.getOutputKeys().get("output2")).isNull();
    }

    @Test
    void givenIllegalMainOutputKeys_whenComposedCommandIsCreated_thenErrorWillBeThrown() throws Throwable {
        setInputKeys();
        Command composedCommand = new Command("composedCommand4", "fdf", composedCommandInput, Map.of("output1", "$(abc.result.body.arr[1])", "output2", "$(illegal.test.test2)"), new ArrayList<>(),
                List.of(new Executor("HttpRequestCommand", "abc", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}))), new Executor("composedCommand2", "abc2", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}), "command", "$(input.command)"))));
        commandRepository.create(composedCommand);

        ExecutionParameters executionParams = new ExecutionParameters();
        executionParams.setInputKeys(Map.of("url", "https://postman-echo.com/post", "timeout", 25, "headers", Map.of("Authentication", "Basic aW1lOnBhcm9sYQ=="), "method", "post", "command", "dir"));
        executionParams.setCommandName("composedCommand4");
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> executionService.executeCommand(executionParams, new Execution()));
        assertThat(exception.getMessage()).isEqualTo("Invalid or missing key [illegal, test, test2]");
    }

    @Test
    void givenIllegalInputKeys_whenComposedCommandIsCreated_thenErrorWillBeThrown() throws Throwable {
        setInputKeys();
        Command composedCommand = new Command("composedCommand5", "fdf", composedCommandInput, Map.of("output1", "$(abc.result.body.arr[1])", "output2", "$(abc2.result)"), new ArrayList<>(),
                List.of(new Executor("HttpRequestCommand", "abc", Map.of("url", "$(input.invalidUrlKey)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}))), new Executor("composedCommand2", "abc2", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}), "command", "$(input.command)"))));
        commandRepository.create(composedCommand);

        ExecutionParameters executionParams = new ExecutionParameters();
        executionParams.setInputKeys(Map.of("url", "https://postman-echo.com/post", "timeout", 25, "headers", Map.of("Authentication", "Basic aW1lOnBhcm9sYQ=="), "method", "post", "command", "dir"));
        executionParams.setCommandName("composedCommand5");
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> executionService.executeCommand(executionParams, new Execution()));
        assertThat(exception.getMessage()).isEqualTo("Invalid or missing key [invalidUrlKey]");
    }

    @Test
    void givenIllegalInputKeyOnExecutorForComposedCommand_whenCommandIsExecuted_thenErrorIsThrown() throws IOException {
        setInputKeys();
        Command composedCommand = new Command("composedCommand6", "fdf", composedCommandInput, Map.of("output1", "$(abc.result.body.arr[1])", "output2", "$(abc2.result)"), new ArrayList<>(),
                List.of(new Executor("HttpRequestCommand", "abc", Map.of("url", "$(input.url)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}))), new Executor("composedCommand2", "abc2", Map.of("url", "$(input.urlIllegal)", "headers", "$(input.headers)", "method", "$(input.method)", "timeout", 25,
                        "body", Map.of("local", 1, "arr", new String[]{"fdf", "gd", "fdf"}), "command", "$(input.command)"))));
        commandRepository.create(composedCommand);

        ExecutionParameters executionParams = new ExecutionParameters();
        executionParams.setInputKeys(Map.of("url", "https://postman-echo.com/post", "timeout", 25, "headers", Map.of("Authentication", "Basic aW1lOnBhcm9sYQ=="), "method", "post", "command", "dir"));
        executionParams.setCommandName("composedCommand6");
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> executionService.executeCommand(executionParams, new Execution()));
        assertThat(exception.getMessage()).isEqualTo("Invalid or missing key [urlIllegal]");
    }
}
