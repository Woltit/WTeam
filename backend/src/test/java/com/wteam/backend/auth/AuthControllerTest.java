package com.wteam.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RefreshTokenRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import com.wteam.backend.exception.user.UserAlreadyExistsException;
import com.wteam.backend.security.TestSecurityConfig;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.security.oauth2.CustomOAuth2UserService;
import com.wteam.backend.security.oauth2.CustomOidcUserService;
import com.wteam.backend.security.oauth2.OAuth2SuccessHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController WebMvcTest")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean AuthService authService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailsService userDetailsService;
    @MockitoBean CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean CustomOidcUserService customOidcUserService;
    @MockitoBean OAuth2SuccessHandler oAuth2SuccessHandler;

    @Test
    @DisplayName("POST /auth/register → 201 with tokens when request is valid")
    void register_whenValid_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest("new@test.com", "password1", "password1");
        AuthResponse resp = new AuthResponse("access-token", "refresh-token");
        when(authService.register(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /auth/register → 409 when email is already taken")
    void register_whenEmailTaken_returns409() throws Exception {
        RegisterRequest req = new RegisterRequest("dup@test.com", "password1", "password1");
        when(authService.register(any())).thenThrow(new UserAlreadyExistsException("dup@test.com"));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/register → 400 when email format is invalid")
    void register_whenInvalidEmail_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest("not-an-email", "password1", "password1");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login → 200 with tokens on valid credentials")
    void login_whenValidCredentials_returns200() throws Exception {
        LoginRequest req = new LoginRequest("user@test.com", "password1");
        AuthResponse resp = new AuthResponse("access-token", "refresh-token");
        when(authService.login(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("POST /auth/login → 401 on bad credentials")
    void login_whenBadCredentials_returns401() throws Exception {
        LoginRequest req = new LoginRequest("user@test.com", "wrongpassword");
        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/refresh → 200 with new access accessToken")
    void refresh_whenValidToken_returns200() throws Exception {
        RefreshTokenRequest req = new RefreshTokenRequest("valid-refresh-token");
        AuthResponse resp = new AuthResponse("new-access-token", "valid-refresh-token");
        when(authService.refreshToken(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }
}
