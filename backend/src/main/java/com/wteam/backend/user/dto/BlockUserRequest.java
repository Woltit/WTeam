package com.wteam.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * The type Block user request.
 */
public record BlockUserRequest(
        @NotBlank(message = "Reason for blocking is essential")
        String reason
) {}
