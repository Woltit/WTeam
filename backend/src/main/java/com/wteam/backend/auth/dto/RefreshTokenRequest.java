package com.wteam.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Об'єкт передачі даних (DTO) для запиту на оновлення токена.
 *
 * @param refreshToken токен оновлення, який буде використано для генерації нового токена доступу
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
