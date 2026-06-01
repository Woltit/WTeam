package com.wteam.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Об'єкт передачі даних (DTO) для запиту на вхід.
 *
 * @param email електронна пошта користувача
 * @param password пароль користувача
 */
public record LoginRequest(
        @NotBlank(message = "Email is essential")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is essential")
        String password
) {}
