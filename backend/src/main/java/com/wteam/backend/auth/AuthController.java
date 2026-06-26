package com.wteam.backend.auth;

import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RefreshTokenRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import com.wteam.backend.cookies.CookieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контролер для обробки запитів автентифікації, таких як реєстрація, вхід та оновлення токена.
 */
@Tag(name = "Автентифікація", description = "API для реєстрації, входу та управління токенами")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(summary = "Реєстрація", description = "Реєструє нового користувача в системі")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            final HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.register(registerRequest);
        cookieService.setRefreshTokenCookie(response, authResponse.refreshToken());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @Operation(summary = "Вхід", description = "Автентифікує користувача за email/паролем та повертає JWT токени")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            final HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.login(loginRequest);
        cookieService.setRefreshTokenCookie(response, authResponse.refreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Оновлення токена", description = "Оновлює токен доступу за допомогою Refresh токена")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            final HttpServletResponse response
    ) {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        AuthResponse authResponse = authService.refreshToken(request);
        cookieService.setRefreshTokenCookie(response, authResponse.refreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Вихід", description = "Видаляє Refresh токен з cookies")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        cookieService.clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}
