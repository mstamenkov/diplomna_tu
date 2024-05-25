package com.example.backend.web;

import com.example.backend.model.Command;
import com.example.backend.service.CommandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class CommandController {

    CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    public CommandController(){}

    @PostMapping("/")
    public ResponseEntity init() throws IOException { //for test purposes
        commandService.commandInit();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/command/{id}")
    public ResponseEntity getCommand(@PathVariable("id") String id) {
        return ResponseEntity.ok(commandService.getCommand(id));
    }

    @GetMapping("/commands")
    public ResponseEntity getAllCommands(@RequestParam(required = false) List<String> tags) {
        tags= tags == null ? List.of() : tags;
        return ResponseEntity.ok(commandService.getAllCommands(tags));
    }

    @PostMapping("/command/create")
    public ResponseEntity createCommand(@RequestBody Command command) throws IOException {
        commandService.createCommand(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(command);
    }

    @PutMapping("/command/edit")
    public ResponseEntity editCommand(@RequestBody Command command) throws IOException {
        return ResponseEntity.ok(commandService.editCommand(command));
    }

    @DeleteMapping("/command/{id}")
    public ResponseEntity deleteCommand(@PathVariable("id") String id) throws IOException {
        commandService.deleteCommand(id);
        return ResponseEntity.ok(null);
    }
}
