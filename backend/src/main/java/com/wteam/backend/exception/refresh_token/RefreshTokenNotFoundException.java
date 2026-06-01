package com.wteam.backend.exception.refresh_token;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class RefreshTokenNotFoundException extends ResourceNotFoundException {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
