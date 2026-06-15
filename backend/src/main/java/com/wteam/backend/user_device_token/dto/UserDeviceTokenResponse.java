package com.wteam.backend.user_device_token.dto;

import com.wteam.backend.common.enums.DeviceType;

import java.time.Instant;

public record UserDeviceTokenResponse(
        Long id,
        String token,
        DeviceType type,
        Instant createdAt
) {}
