package com.wteam.backend.user_device_token;

import com.wteam.backend.user_device_token.dto.UserDeviceTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class UserDeviceTokenMapper {

    public UserDeviceTokenResponse toResponse(UserDeviceToken userDeviceToken) {
        return new UserDeviceTokenResponse(
                userDeviceToken.getId(),
                userDeviceToken.getToken(),
                userDeviceToken.getType(),
                userDeviceToken.getCreatedAt()
        );
    }
}
