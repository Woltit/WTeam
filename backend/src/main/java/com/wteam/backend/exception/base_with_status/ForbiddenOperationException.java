package com.wteam.backend.exception.base_with_status;

import com.wteam.backend.exception.base.AppException;
import org.springframework.http.HttpStatus;

public abstract class ForbiddenOperationException extends AppException {
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenOperationException(String message) {
        super(message, STATUS);
    }
}
