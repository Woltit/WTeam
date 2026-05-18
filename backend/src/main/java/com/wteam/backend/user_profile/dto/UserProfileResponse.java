package com.wteam.backend.user_profile.dto;

import com.wteam.backend.common.enums.VerificationStatus;

import java.time.LocalDate;

public record UserProfileResponse(
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate,
        String phoneNumber,
        String bio,
        VerificationStatus verificationStatus
) {}
