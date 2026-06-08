package com.wteam.backend.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank
        @Size(max = 2000)
        String messageText
) {}
