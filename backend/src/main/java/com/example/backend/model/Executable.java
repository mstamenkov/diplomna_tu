package com.example.backend.model;


import java.util.Map;

public interface Executable {
    Map<String, Object> execute(Map<String, Object> inputParams);
}
