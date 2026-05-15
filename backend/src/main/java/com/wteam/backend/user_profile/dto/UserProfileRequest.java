package com.wteam.backend.user_profile.dto;

import java.time.LocalDate;

public record UserProfileRequest(
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate,
        String phone,
        String bio
) {}
