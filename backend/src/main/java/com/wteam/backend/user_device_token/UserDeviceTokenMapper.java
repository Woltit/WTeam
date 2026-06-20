package com.wteam.backend.user_device_token;

import com.wteam.backend.common.interfaces.Mapper;
import com.wteam.backend.user_device_token.dto.UserDeviceTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class UserDeviceTokenMapper implements Mapper<Void, UserDeviceTokenResponse, UserDeviceToken> {

    @Override
    public UserDeviceTokenResponse toResponse(UserDeviceToken userDeviceToken) {
        return new UserDeviceTokenResponse(
                userDeviceToken.getId(),
                userDeviceToken.getToken(),
                userDeviceToken.getType(),
                userDeviceToken.getCreatedAt()
        );
    }

    @Override
    public UserDeviceToken toEntity(Void dto) {
        return null;
    }
}
