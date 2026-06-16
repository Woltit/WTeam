package com.wteam.backend.websocket;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtChannelInterceptor Unit Tests")
class JwtChannelInterceptorTest {

    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @InjectMocks private JwtChannelInterceptor interceptor;

    private MessageChannel channel;
    private SecurityUser securityUser;

    @BeforeEach
    void setUp() {
        channel = mock(MessageChannel.class);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setRole(Role.USER);
        user.setActive(true);
        securityUser = SecurityUser.create(user, null);
    }

    private Message<?> buildConnectMessage(String authHeader) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        if (authHeader != null) {
            accessor.addNativeHeader("Authorization", authHeader);
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Message<?> buildSendMessage() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    @Test
    @DisplayName("preSend should set Principal on CONNECT frame with valid Bearer accessToken")
    void preSend_whenConnectWithValidToken_shouldSetPrincipal() {
        Message<?> message = buildConnectMessage("Bearer valid-accessToken");

        when(jwtService.extractUsername("valid-accessToken")).thenReturn("user@test.com");
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(securityUser);
        when(jwtService.isTokenValid("valid-accessToken", securityUser)).thenReturn(true);

        Message<?> result = interceptor.preSend(message, channel);

        assertNotNull(result);
        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);
        assertNotNull(resultAccessor.getUser());
    }

    @Test
    @DisplayName("preSend should NOT block message when accessToken is invalid — just skips auth")
    void preSend_whenConnectWithInvalidToken_shouldPassMessageThrough() {
        Message<?> message = buildConnectMessage("Bearer bad-accessToken");

        when(jwtService.extractUsername("bad-accessToken")).thenThrow(new RuntimeException("invalid"));

        Message<?> result = interceptor.preSend(message, channel);

        assertNotNull(result, "Message must be passed through even with invalid accessToken");
    }

    @Test
    @DisplayName("preSend should ignore non-CONNECT frames without touching the principal")
    void preSend_whenNonConnectFrame_shouldPassThroughUnchanged() {
        Message<?> message = buildSendMessage();

        Message<?> result = interceptor.preSend(message, channel);

        assertNotNull(result);
        verifyNoInteractions(jwtService, userDetailsService);
    }
}
