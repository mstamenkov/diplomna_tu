package com.example.backend.exception;

public class ExecutableException extends RuntimeException {
    public ExecutableException(Throwable cause) {
        super(cause);
    }

    public ExecutableException(String message, Throwable cause) {
        super(message, cause);
    }
}
