package com.wteam.backend.exception.auth;

import com.wteam.backend.exception.base_with_status.InternalServerErrorException;

public class ProviderNotFoundException extends InternalServerErrorException {
    public ProviderNotFoundException(String message) {
        super(message);
    }
}
