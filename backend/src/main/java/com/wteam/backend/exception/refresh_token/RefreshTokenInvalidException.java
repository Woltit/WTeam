package com.wteam.backend.exception.refresh_token;

import com.wteam.backend.exception.base_with_status.UnauthorizedException;

public class RefreshTokenInvalidException extends UnauthorizedException {

    public RefreshTokenInvalidException() {
        super("Refresh accessToken is invalid, expired, or has been revoked");
    }

    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}