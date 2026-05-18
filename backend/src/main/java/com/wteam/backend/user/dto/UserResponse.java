package com.wteam.backend.user.dto;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user_profile.dto.UserProfileResponse;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        Role role,
        UserProfileResponse profile,
        Instant createdAt
) {}
