package com.wteam.backend.exception.user;

import com.wteam.backend.exception.base_with_status.BadRequestException;

public class ProfileAlreadyVerifiedException extends BadRequestException {
    public ProfileAlreadyVerifiedException(String message) {
        super(message);
    }
}
