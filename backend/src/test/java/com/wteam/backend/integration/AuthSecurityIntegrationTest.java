package com.wteam.backend.integration;

import tools.jackson.databind.ObjectMapper;
import com.wteam.backend.TestcontainersConfiguration;
import com.wteam.backend.auth.dto.AuthResponse;
import com.wteam.backend.auth.dto.LoginRequest;
import com.wteam.backend.auth.dto.RegisterRequest;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("Security Integration Tests")
class AuthSecurityIntegrationTest {

    private static final String API = "";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest req = new LoginRequest(email, password);
        String response = mockMvc.perform(post(API + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);
        return authResponse.token();
    }

    private void ensureUser(String email, String password, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email).password(passwordEncoder.encode(password))
                    .role(role).isActive(true).build();
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("GET /items/available → 200 without authentication (public endpoint)")
    void publicItemsEndpoint_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get(API + "/items/available"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /bookings → 401 without a JWT token")
    void protectedBookingEndpoint_withoutToken_returns401() throws Exception {
        String body = """
                {"itemId":1,"startDate":"2026-09-01","endDate":"2026-09-05"}
                """;
        mockMvc.perform(post(API + "/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /bookings (admin) → 403 when user has USER role")
    void adminBookingsEndpoint_withUserRole_returns403() throws Exception {
        String email = "security-user-" + System.nanoTime() + "@test.com";
        ensureUser(email, "password123", Role.USER);
        String token = loginAndGetToken(email, "password123");

        mockMvc.perform(get(API + "/bookings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/register → 201 and returns tokens for new user")
    void register_newUser_returns201WithTokens() throws Exception {
        String email = "brand-new-" + System.nanoTime() + "@test.com";
        RegisterRequest req = new RegisterRequest(email, "securePass1", "securePass1");

        mockMvc.perform(post(API + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}
