package com.wteam.backend.exception.base_with_status;

import com.wteam.backend.exception.base.AppException;
import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends AppException {
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message, STATUS);
    }
}
