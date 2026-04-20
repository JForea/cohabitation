package com.example.backend.handlers;

import com.example.backend.dtos.out.common.ErrorDetails;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.exceptions.StateConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorDetails> createErrorResponse(Exception e, String type, HttpStatus status) {
        ErrorDetails details = new ErrorDetails(
                type,
                e.getMessage(),
                status.value()
        );
        return new ResponseEntity<>(details, status);
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ErrorDetails> handleUsernameNotFound(Exception e) {
        return createErrorResponse(e, "NotFoundException", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            StateConflictException.class
    })
    public ResponseEntity<ErrorDetails> handleConflict(Exception e) {
        return createErrorResponse(e, "StateConflictException", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ErrorDetails> handleAccessForbidden(Exception e) {
        return createErrorResponse(e, "AccessForbiddenException", HttpStatus.FORBIDDEN);
    }

}
