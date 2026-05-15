package com.wteam.backend.user.dto;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;

import java.time.LocalDate;

public record UserResponse(
        String email,
        Role role,
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate,
        String phone,
        String bio,
        VerificationStatus verificationStatus
) {}
