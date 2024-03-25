package com.example.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<CustomErrorResponse> handleGenericNoSuchElementException(NoSuchElementException e) {
        e.printStackTrace();
        return new ResponseEntity<>(new CustomErrorResponse(NOT_FOUND, e), NOT_FOUND);
    }

    @ExceptionHandler(value = {IOException.class, ClassCastException.class, InterruptedException.class})
    public ResponseEntity<CustomErrorResponse> handleInternalException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new CustomErrorResponse(INTERNAL_SERVER_ERROR, e), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        e.printStackTrace();
        return new ResponseEntity<>(new CustomErrorResponse(BAD_REQUEST, e), BAD_REQUEST);
    }

    @ExceptionHandler(value = ForbiddenOperationException.class)
    public ResponseEntity<CustomErrorResponse> handleForbiddenOperationException(ForbiddenOperationException e) {
        e.printStackTrace();
        return new ResponseEntity<>(new CustomErrorResponse(FORBIDDEN, e), FORBIDDEN);
    }
}
