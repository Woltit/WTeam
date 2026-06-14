package com.wteam.backend.ai_session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.wteam.backend.common.validation.ValidationConstants.AiSession.QUERY_MAX_LENGTH;

public record AiQueryRequest(
        @NotBlank
        @Size(max = QUERY_MAX_LENGTH)
        String query
) {}
