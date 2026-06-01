package com.wteam.backend.exception.base_with_status;

import com.wteam.backend.exception.base.AppException;
import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends AppException {
    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    public UnauthorizedException(String message) {
        super(message, STATUS);
    }
}