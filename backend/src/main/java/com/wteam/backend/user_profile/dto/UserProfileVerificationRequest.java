package com.wteam.backend.user_profile.dto;

import com.wteam.backend.common.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;

public record UserProfileVerificationRequest(
    @NotNull
    VerificationStatus verificationStatus,
    String comment
) {}
