package com.wteam.backend.chat_room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wteam.backend.chat_room.dto.ChatRoomResponse;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.exception.chat.ChatAccessDeniedException;
import com.wteam.backend.message.MessageService;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.security.oauth2.CustomOAuth2UserService;
import com.wteam.backend.security.oauth2.CustomOidcUserService;
import com.wteam.backend.security.oauth2.OAuth2SuccessHandler;
import com.wteam.backend.security.TestSecurityConfig;
import com.wteam.backend.user.User;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomController.class)
@Import(TestSecurityConfig.class)
@DisplayName("ChatRoomController WebMvcTest")
class ChatRoomControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean ChatRoomService chatRoomService;
    @MockitoBean MessageService messageService;
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

    private ChatRoomResponse sampleRoom() {
        return new ChatRoomResponse(1L, 10L, "Drill", 2L, "owner@test.com", Instant.now());
    }

    @Test
    @DisplayName("POST /chat-rooms/booking/{id} → 200 for booking participant")
    void getOrCreate_whenParticipant_returns200() throws Exception {
        when(chatRoomService.getOrCreateByBookingId(eq(10L), eq(1L))).thenReturn(sampleRoom());

        mockMvc.perform(post("/chat-rooms/booking/10")
                        .with(csrf())
                        .with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /chat-rooms/booking/{id} → 401 when unauthenticated")
    void getOrCreate_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/chat-rooms/booking/10")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /chat-rooms/booking/{id} → 403 when user is not a booking participant")
    void getOrCreate_whenNotParticipant_returns403() throws Exception {
        when(chatRoomService.getOrCreateByBookingId(eq(10L), eq(1L)))
                .thenThrow(new ChatAccessDeniedException());

        mockMvc.perform(post("/chat-rooms/booking/10")
                        .with(csrf())
                        .with(user(regularUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /chat-rooms → 200 returns current user's rooms")
    void getMyRooms_whenAuthenticated_returns200() throws Exception {
        when(chatRoomService.getRoomsForUser(eq(1L))).thenReturn(List.of(sampleRoom()));

        mockMvc.perform(get("/chat-rooms")
                        .with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /chat-rooms/{id}/messages → 200 for room participant")
    void getMessages_whenParticipant_returns200() throws Exception {
        MessageResponse msg = new MessageResponse(1L, 1L, "user@test.com", "Hello", false, Instant.now());
        when(messageService.getMessages(eq(1L), eq(1L))).thenReturn(List.of(msg));

        mockMvc.perform(get("/chat-rooms/1/messages")
                        .with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageText").value("Hello"));
    }

    @Test
    @DisplayName("POST /chat-rooms/{id}/messages → 201 when message is sent")
    void sendMessage_whenAuthenticated_returns201() throws Exception {
        MessageResponse msg = new MessageResponse(1L, 1L, "user@test.com", "Hi!", false, Instant.now());
        when(messageService.sendMessage(eq(1L), eq(1L), any())).thenReturn(msg);

        MessageRequest req = new MessageRequest("Hi!");

        mockMvc.perform(post("/chat-rooms/1/messages")
                        .with(csrf())
                        .with(user(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageText").value("Hi!"));
    }
}
