package com.wteam.backend.user_profile.dto;

import java.math.BigDecimal;

public record PublicProfileResponse(
        String lastName,
        String firstName,
        String middleName,
        String bio,
        String avatarUrl,
        BigDecimal renterTrustScore,
        BigDecimal ownerTrustScore,
        Integer totalSuccessfulRents
) {}
