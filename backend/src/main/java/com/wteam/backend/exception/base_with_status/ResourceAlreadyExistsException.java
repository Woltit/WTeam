package com.wteam.backend.exception.base_with_status;

import com.wteam.backend.exception.base.AppException;
import org.springframework.http.HttpStatus;

public abstract class ResourceAlreadyExistsException extends AppException {
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public ResourceAlreadyExistsException(String message) {
        super(message, STATUS);
    }
}
