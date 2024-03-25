package com.example.backend.model;

import com.example.backend.exception.ExecutableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.backend.util.Constants.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class HttpRequestExecutable implements Executable {
    HttpClient client;

    public HttpRequestExecutable(HttpClient client) {
        this.client = client;
    }

    public HttpRequestExecutable() {
        client = HttpClient.newHttpClient();
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> inputParams) {
        try {
            HttpResponse<String> response = client.send(requestBuilder(inputParams), HttpResponse.BodyHandlers.ofString());
            System.out.println(response.headers());
            System.out.println(inputParams.get(HEADERS));
            return resolveOutputParameters(response);
        } catch (IOException | InterruptedException e) {
            throw new ExecutableException(e);
        } catch (URISyntaxException e){
            throw new IllegalArgumentException(e);
        }
    }

    private void addHeadersFromMap(LinkedHashMap<String, Object> headers, HttpRequest.Builder request) {
        headers.keySet().forEach(key -> {
            Object valueType = headers.get(key);
            if (valueType instanceof List) {
                for (String value : (List<String>) headers.get(key)) {
                    request.header(key, value);
                }
            } else request.header(key, (String) valueType);
        });
    }

    private HttpRequest requestBuilder(Map<String, Object> inputParams) throws JsonProcessingException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(inputParams.get(BODY));
        String HTTPMethod = inputParams.get(HTTP_METHOD).toString().toUpperCase().replaceAll(" ","");
        if(HttpMethod.resolve(HTTPMethod) == null)throw new IllegalArgumentException("Illegal HTTP method");
        HttpRequest.Builder requestBuild = HttpRequest.newBuilder()
                .uri(new URI((String) inputParams.get(URL)))
                .method(HTTPMethod, HttpRequest.BodyPublishers.ofString(body));
        if (inputParams.containsKey(TIMEOUT)) {
            requestBuild.timeout(Duration.ofSeconds((int) inputParams.get(TIMEOUT)));
        }
        addHeadersFromMap((LinkedHashMap<String, Object>) inputParams.get(HEADERS), requestBuild);
        return requestBuild.build();
    }

    private Map<String, Object> resolveOutputParameters(HttpResponse<String> response) throws IOException {
        Map<String, Object> outputKeys = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = new HashMap<>();
        if(isNotEmpty(response.body())) {
            try {
                HashMap<String, HashMap<String, Object>> buffer = mapper.readValue(response.body(), HashMap.class);
                result.put(BODY, buffer.get(DATA));
            }catch (JsonProcessingException e){
                result.put(BODY, response.body());
            }
        }
        if(response.statusCode() >= 200 && response.statusCode() <= 299) {
            outputKeys.put(RESPONSE_CODE, response.statusCode());
            result.put(HEADERS, response.headers().map());
            outputKeys.put(OUTPUT, result);

        }else {
            buildError(response.statusCode());
        }
        return outputKeys;
    }

    private void buildError(int code) throws IOException {
        throw new IOException(format("Invalid request. Status code %s", code));
    }
}
