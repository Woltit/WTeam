package com.wteam.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record BlockUserRequest(
        @NotBlank(message = "Reason for blocking is essential")
        String reason
) {}
