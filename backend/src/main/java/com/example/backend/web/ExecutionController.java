package com.example.backend.web;

import com.example.backend.dto.ExecutionParameters;
import com.example.backend.dto.ExecutionResult;
import com.example.backend.service.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExecutionController {
    ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    public ExecutionController(){}

    @PostMapping("/execution/execute")
    @ResponseBody
    public ExecutionResult execute(@RequestBody ExecutionParameters parameters) throws Throwable {
            return new ExecutionResult(executionService.initCommandExecution(parameters));
    }

    @GetMapping("/execution/{id}")
    public ResponseEntity getExecution(@PathVariable("id") String id) {
            return ResponseEntity.ok(executionService.getExecution(id));

    }

    @GetMapping("/executions")
    public ResponseEntity getAllExecutions(@RequestParam(required = false) List<String> tags) {
        tags= tags == null ? List.of() : tags;
        return ResponseEntity.ok(executionService.getAllExecutions(tags));
    }
}
