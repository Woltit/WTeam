package com.wteam.backend.exception.user_device_token;

import com.wteam.backend.exception.base_with_status.ForbiddenOperationException;

public class UserDeviceTokenAccessDeniedException extends ForbiddenOperationException {
    public UserDeviceTokenAccessDeniedException() {
        super("You do not have permission to delete this device token");
    }
}
