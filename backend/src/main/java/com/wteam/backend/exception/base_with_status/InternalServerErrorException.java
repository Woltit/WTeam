package com.wteam.backend.exception.base_with_status;

import com.wteam.backend.exception.base.AppException;
import org.springframework.http.HttpStatus;

public abstract class InternalServerErrorException extends AppException {
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerErrorException(String message) {
        super(message, STATUS);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }
}
