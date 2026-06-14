package com.wteam.backend.security;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private static final String SIGNING_KEY =
            "eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4";
    private static final long EXPIRATION_MS = 3_600_000L;

    private JwtService jwtService;
    private SecurityUser securityUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", SIGNING_KEY);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION_MS);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setActive(true);
        securityUser = SecurityUser.create(user, null);
    }

    @Test
    @DisplayName("generateToken should return non-blank JWT string")
    void generateToken_shouldReturnValidJwtString() {
        String token = jwtService.generateToken(securityUser);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length, "JWT must have 3 parts separated by dots");
    }

    @Test
    @DisplayName("extractUsername should return email from token subject")
    void extractUsername_shouldReturnEmail() {
        String token = jwtService.generateToken(securityUser);

        String extracted = jwtService.extractUsername(token);

        assertEquals("test@example.com", extracted);
    }

    @Test
    @DisplayName("isTokenValid should return true for fresh token with matching user")
    void isTokenValid_whenFreshTokenAndMatchingUser_shouldReturnTrue() {
        String token = jwtService.generateToken(securityUser);

        assertTrue(jwtService.isTokenValid(token, securityUser));
    }

    @Test
    @DisplayName("isTokenValid should return false when username does not match")
    void isTokenValid_whenUsernameMismatch_shouldReturnFalse() {
        String token = jwtService.generateToken(securityUser);

        User other = new User();
        other.setId(2L);
        other.setEmail("other@example.com");
        other.setRole(Role.USER);
        other.setActive(true);
        UserDetails otherUser = SecurityUser.create(other, null);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    @DisplayName("isTokenValid should return false for an expired token")
    void isTokenValid_whenTokenExpired_shouldReturnFalse() {
        JwtService expiredJwtService = new JwtService();
        ReflectionTestUtils.setField(expiredJwtService, "jwtSigningKey", SIGNING_KEY);
        ReflectionTestUtils.setField(expiredJwtService, "expiration", -1000L);

        String expiredToken = expiredJwtService.generateToken(securityUser);

        assertFalse(jwtService.isTokenValid(expiredToken, securityUser));
    }
}
