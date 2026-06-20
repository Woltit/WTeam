package com.wteam.backend.auth;

import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RefreshTokenRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Автентифікація", description = "API для реєстрації, входу та управління токенами")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Реєстрація", description = "Реєструє нового користувача в системі")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(registerRequest));
    }

    @Operation(summary = "Вхід", description = "Автентифікує користувача за email/паролем та повертає JWT токени")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "Оновлення токена", description = "Оновлює токен доступу за допомогою Refresh токена")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
}
