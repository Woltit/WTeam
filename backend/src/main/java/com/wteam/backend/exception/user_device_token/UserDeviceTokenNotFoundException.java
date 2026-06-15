package com.wteam.backend.exception.user_device_token;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class UserDeviceTokenNotFoundException extends ResourceNotFoundException {
    public UserDeviceTokenNotFoundException(String token) {
        super("Device token not found: " + token);
    }
}
