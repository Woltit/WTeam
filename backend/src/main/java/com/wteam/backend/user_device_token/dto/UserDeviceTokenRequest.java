package com.wteam.backend.user_device_token.dto;

import com.wteam.backend.common.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDeviceTokenRequest(
        @NotBlank
        String token,

        @NotNull
        DeviceType type
) {}
