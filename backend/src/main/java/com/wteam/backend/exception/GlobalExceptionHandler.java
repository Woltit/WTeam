package com.wteam.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }


    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, message));
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String detailedMessage) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, message, detailedMessage));
    }
}
