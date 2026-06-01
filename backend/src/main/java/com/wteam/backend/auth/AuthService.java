package com.wteam.backend.auth;

import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RefreshTokenRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import com.wteam.backend.common.enums.AuthProvider;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.exception.user.UserAlreadyExistsException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.refresh_token.RefreshToken;
import com.wteam.backend.refresh_token.RefreshTokenService;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для керування логікою автентифікації, включаючи реєстрацію, вхід та обробку токенів.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Реєструє нового користувача.
     *
     * @param registerRequest дані для реєстрації
     * @return відповідь з токенами доступу та оновлення
     * @throws UserAlreadyExistsException якщо користувач з такою електронною поштою вже існує
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new UserAlreadyExistsException(registerRequest.email());
        }

        User user = User.builder()
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .authProvider(AuthProvider.LOCAL)
                .role(Role.USER)
                .isActive(true)
                .build();

        UserProfile userProfile = new UserProfile();
        user.setUserProfile(userProfile);

        userRepository.save(user);

        SecurityUser securityUser = SecurityUser.create(user, null);
        String jwtToken = jwtService.generateToken(securityUser);

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
        return new AuthResponse(jwtToken, refreshToken.getTokenHash());
    }

    /**
     * Автентифікує користувача на основі електронної пошти та пароля.
     *
     * @param loginRequest облікові дані для входу
     * @return відповідь з токенами доступу та оновлення
     * @throws UserNotFoundException якщо користувача не знайдено після успішної автентифікації
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.email()));

        SecurityUser securityUser = SecurityUser.create(user, null);
        String jwtToken = jwtService.generateToken(securityUser);

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
        return new AuthResponse(jwtToken, refreshToken.getTokenHash());
    }

    /**
     * Обробляє токен оновлення для створення нового токена доступу.
     *
     * @param refreshTokenRequest запит на оновлення токена
     * @return відповідь з новим токеном доступу та тим самим токеном оновлення
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.processRefreshToken(refreshTokenRequest.refreshToken());

        User user = refreshToken.getUser();

        SecurityUser securityUser = SecurityUser.create(user, null);
        String newAccessToken = jwtService.generateToken(securityUser);

        return new AuthResponse(newAccessToken, refreshToken.getTokenHash());
    }
}
