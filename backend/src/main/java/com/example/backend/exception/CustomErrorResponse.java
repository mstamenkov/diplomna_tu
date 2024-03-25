package com.example.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class CustomErrorResponse {
    LocalDateTime timestamp;
    HttpStatus error;
    int status;
    String reason;

    public CustomErrorResponse(HttpStatus error, Exception e) {
        this.error = error;
        this.status = error.value();
        if(e.getMessage().contains(":")){
            this.reason = e.getMessage().substring(e.getMessage().indexOf(':') + 1);
        }else {
            this.reason = e.getMessage();
        }

        timestamp = LocalDateTime.now();
    }

    public HttpStatus getError() {
        return error;
    }

    public void setError(HttpStatus error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
