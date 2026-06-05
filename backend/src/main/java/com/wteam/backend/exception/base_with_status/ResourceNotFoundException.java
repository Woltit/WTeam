package com.wteam.backend.exception.base_with_status;

import com.wteam.backend.exception.base.AppException;
import org.springframework.http.HttpStatus;

public abstract class ResourceNotFoundException extends AppException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(String message) {
        super(message, STATUS);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, STATUS);
    }

    public ResourceNotFoundException(String entityName, Long id) {
        super("%s with id %d was not found".formatted(entityName, id), STATUS);
    }
}
