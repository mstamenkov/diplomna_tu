package com.example.backend.exception;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(Throwable cause) {
        super(cause);
    }

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
