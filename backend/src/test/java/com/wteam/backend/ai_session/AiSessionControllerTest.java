package com.wteam.backend.ai_session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wteam.backend.ai_session.dto.AiQueryRequest;
import com.wteam.backend.ai_session.dto.AiQueryResponse;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.TestSecurityConfig;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.security.oauth2.CustomOAuth2UserService;
import com.wteam.backend.security.oauth2.CustomOidcUserService;
import com.wteam.backend.security.oauth2.OAuth2SuccessHandler;
import com.wteam.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiSessionController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AiSessionController WebMvcTest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AiSessionControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    @MockitoBean AiSessionService aiSessionService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailsService userDetailsService;
    @MockitoBean CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean CustomOidcUserService customOidcUserService;
    @MockitoBean OAuth2SuccessHandler oAuth2SuccessHandler;

    private SecurityUser regularUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L); user.setEmail("user@test.com");
        user.setRole(Role.USER); user.setActive(true);
        regularUser = SecurityUser.create(user, null);
    }

    @Test
    @DisplayName("POST /ai/recommend → 200 without authentication (public endpoint)")
    void recommend_withoutAuth_returns200() throws Exception {
        AiQueryResponse resp = new AiQueryResponse(1L, "Try a drill", List.of(1L));
        when(aiSessionService.processQuery(any(), isNull())).thenReturn(resp);

        AiQueryRequest req = new AiQueryRequest("I need a drill");

        mockMvc.perform(post("/ai/recommend")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aiResponse").value("Try a drill"));
    }

    @Test
    @DisplayName("POST /ai/recommend → 200 with authentication (userId stored in session)")
    void recommend_withAuth_returns200() throws Exception {
        AiQueryResponse resp = new AiQueryResponse(1L, "Here are your options", List.of(2L, 3L));
        when(aiSessionService.processQuery(any(), any())).thenReturn(resp);

        AiQueryRequest req = new AiQueryRequest("tent for camping");

        mockMvc.perform(post("/ai/recommend")
                        .with(csrf())
                        .with(user(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedItemIds.length()").value(2));
    }

    @Test
    @DisplayName("POST /ai/recommend → 400 when query is blank")
    void recommend_whenQueryBlank_returns400() throws Exception {
        String body = """
                {"query":""}
                """;

        mockMvc.perform(post("/ai/recommend")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /ai/recommend → 400 when query exceeds 1000 characters")
    void recommend_whenQueryTooLong_returns400() throws Exception {
        String longQuery = "x".repeat(1001);
        AiQueryRequest req = new AiQueryRequest(longQuery);

        mockMvc.perform(post("/ai/recommend")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
