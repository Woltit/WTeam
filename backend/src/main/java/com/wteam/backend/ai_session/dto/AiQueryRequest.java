package com.wteam.backend.ai_session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiQueryRequest(
        @NotBlank
        @Size(max = 1000)
        String query
) {}
