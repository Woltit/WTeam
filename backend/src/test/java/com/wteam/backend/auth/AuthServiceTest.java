package com.wteam.backend.auth;

import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RefreshTokenRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import com.wteam.backend.exception.user.UserAlreadyExistsException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.refresh_token.RefreshToken;
import com.wteam.backend.refresh_token.RefreshTokenService;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register should save user and return tokens when email is unique")
    void register_whenEmailUnique_shouldRegisterAndReturnTokens() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password123", "password123");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash("dummy-hash");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-pwd");
        when(jwtService.generateToken(any(SecurityUser.class))).thenReturn("access-accessToken");
        when(refreshTokenService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("access-accessToken", response.accessToken());
        assertEquals("dummy-hash", response.refreshToken());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register should throw UserAlreadyExistsException when email exists")
    void register_whenEmailExists_shouldThrowException() {
        RegisterRequest request = new RegisterRequest("existing@example.com", "password123", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("login should authenticate, retrieve user, and return tokens on success")
    void login_whenCredentialsCorrect_shouldReturnTokens() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = new User();
        user.setEmail(request.email());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash("dummy-hash");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(SecurityUser.class))).thenReturn("access-accessToken");
        when(refreshTokenService.generateRefreshToken(user)).thenReturn(refreshToken);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-accessToken", response.accessToken());
        assertEquals("dummy-hash", response.refreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login should throw UserNotFoundException when user is not found in database")
    void login_whenUserNotFoundInDb_shouldThrowException() {
        LoginRequest request = new LoginRequest("unknown@example.com", "password123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("refreshToken should return new access accessToken for valid refresh accessToken")
    void refreshToken_whenValid_shouldReturnNewAccessToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-accessToken");
        User user = new User();
        user.setEmail("user@example.com");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash("valid-refresh-accessToken-hash");

        when(refreshTokenService.processRefreshToken(request.refreshToken())).thenReturn(refreshToken);
        when(jwtService.generateToken(any(SecurityUser.class))).thenReturn("new-access-accessToken");

        AuthResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-access-accessToken", response.accessToken());
        assertEquals("valid-refresh-accessToken-hash", response.refreshToken());
    }
}
