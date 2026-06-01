package com.wteam.backend.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {
    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AppException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
