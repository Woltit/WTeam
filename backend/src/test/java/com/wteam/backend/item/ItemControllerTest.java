package com.wteam.backend.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.item.dto.ItemRequest;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.item_image.ItemImageService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(TestSecurityConfig.class)
@DisplayName("ItemController WebMvcTest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean ItemService itemService;
    @MockitoBean ItemImageService itemImageService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailsService userDetailsService;
    @MockitoBean CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean CustomOidcUserService customOidcUserService;
    @MockitoBean OAuth2SuccessHandler oAuth2SuccessHandler;

    private SecurityUser regularUser;
    private SecurityUser adminUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L); user.setEmail("user@test.com");
        user.setRole(Role.USER); user.setActive(true);
        regularUser = SecurityUser.create(user, null);

        User admin = new User();
        admin.setId(2L); admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN); admin.setActive(true);
        adminUser = SecurityUser.create(admin, null);
    }

    private ItemRequest validItemRequest() {
        return new ItemRequest(
                1L, "Drill", "A power drill",
                List.of("tool"), ItemCondition.IDEAL,
                BigDecimal.valueOf(50), BigDecimal.valueOf(300),
                BigDecimal.valueOf(100), "Kyiv", "123 Test St",
                BigDecimal.valueOf(50.45), BigDecimal.valueOf(30.52)
        );
    }

    @Test
    @DisplayName("GET /items/available → 200 without authentication (public endpoint)")
    void getAvailableItems_withoutAuth_returns200() throws Exception {
        when(itemService.getAllItemsWhichAreAvailable(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/items/available"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /items → 401 when request is unauthenticated")
    void createItem_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /items → 201 for authenticated user with valid item")
    void createItem_whenAuthenticated_returns201() throws Exception {
        ItemResponse resp = new ItemResponse(
                1L, 1L, null, 1L, "Drill", "A power drill",
                List.of("tool"), ItemCondition.IDEAL,
                BigDecimal.valueOf(50), BigDecimal.valueOf(300),
                BigDecimal.valueOf(100), RentingStatus.AVAILABLE,
                "Kyiv", "123 Test St", BigDecimal.valueOf(50.45), BigDecimal.valueOf(30.52),
                true, Instant.now(), Instant.now(), List.of()
        );
        when(itemService.createItem(any(), any())).thenReturn(resp);

        mockMvc.perform(post("/items")
                        .with(csrf())
                        .with(user(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PATCH /items/{id}/verification → 403 for non-admin user")
    void setVerification_whenNotAdmin_returns403() throws Exception {
        mockMvc.perform(patch("/items/1/verification")
                        .with(csrf())
                        .with(user(regularUser))
                        .param("verified", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /items/{id}/verification → 200 for admin user")
    void setVerification_whenAdmin_returns200() throws Exception {
        ItemResponse resp = mock(ItemResponse.class);
        when(itemService.setItemVerified(anyLong(), anyBoolean())).thenReturn(resp);

        mockMvc.perform(patch("/items/1/verification")
                        .with(csrf())
                        .with(user(adminUser))
                        .param("verified", "true"))
                .andExpect(status().isOk());
    }
}
