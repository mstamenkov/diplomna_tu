package com.example.backend.model;

import com.example.backend.exception.ExecutableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.backend.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
class HttpRequestExecutableTest {
    private static final HttpRequestExecutable executable = new HttpRequestExecutable();
    private static final Map<String, Object> headers = new LinkedHashMap<>();
    private static final Map<String, Object> body = new LinkedHashMap<>();
    private static final Map<String, Object> inputKeys = new HashMap<>();

    @BeforeEach
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

    @Test
    void givenValidInputKeys_whenExecutingHttpRequestCommand_thenRequestResultIsReturned() {
        Map<String, Object> result = executable.execute(inputKeys);
        Map<String, Object> bodyResult;
        bodyResult = (Map<String, Object>) result.get(OUTPUT);
        assertThat(result.get(RESPONSE_CODE)).isEqualTo(200);
        assertThat(bodyResult.get(BODY)).isEqualTo(body);
    }

    @Test
    void givenInvalidURL_whenExecutingHttpRequestCommand_thenExceptionIsThrown() {
        inputKeys.replace(URL, "test");
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> executable.execute(inputKeys));
        assertThat(exception.getMessage()).isEqualTo("URI with undefined scheme");
    }

    @Test
    void givenWrongMethod_whenExecutingHttpRequestCommand_thenNotFoundIsReturned() {
        inputKeys.replace(HTTP_METHOD, "GET");
        Throwable throwable = assertThrows(ExecutableException.class, () -> executable.execute(inputKeys).get(RESPONSE_CODE));
        assertThat(throwable.getMessage()).contains("Invalid request. Status code 404");
    }

    @Mock
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void whenHttpRequestExecutableInternalError_thenExceptionIsThrown() throws IOException, InterruptedException {
        Mockito.when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IllegalArgumentException("test message"));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new HttpRequestExecutable(client).execute(inputKeys));
        assertThat(exception.getMessage()).isEqualTo("test message");
    }
}
