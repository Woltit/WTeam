package com.wteam.backend.auth.dto;

/**
 * Об'єкт передачі даних (DTO) для відповіді автентифікації, що містить JWT та токен оновлення.
 *
 * @param token токен доступу (JWT)
 * @param refreshToken токен оновлення
 */
public record AuthResponse(
        String token,
        String refreshToken
) {}
