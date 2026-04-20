package com.example.backend.exceptions;

public class StateConflictException extends RuntimeException {
    public StateConflictException(String message) {
        super(message);
    }
}
