package com.wteam.backend.auth;

import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RefreshTokenRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контролер для обробки запитів автентифікації, таких як реєстрація, вхід та оновлення токена.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Реєструє нового користувача в системі.
     *
     * @param registerRequest дані для реєстрації
     * @return відповідь з токеном доступу та токеном оновлення
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(registerRequest));
    }

    /**
     * Автентифікує користувача та повертає токени доступу та оновлення.
     *
     * @param loginRequest облікові дані для входу
     * @return відповідь з токеном доступу та токеном оновлення
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Оновлює токен доступу, використовуючи наданий токен оновлення.
     *
     * @param refreshTokenRequest запит на оновлення токена
     * @return відповідь з новим токеном доступу та токеном оновлення
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
            ) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
}
