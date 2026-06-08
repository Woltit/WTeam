package com.wteam.backend.websocket;

import com.wteam.backend.message.MessageService;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;

    /**
     * Клієнт надсилає на: /app/chat-rooms/{roomId}/send
     * Повідомлення транслюється на: /topic/chat-rooms/{roomId}
     */
    @MessageMapping("/chat-rooms/{roomId}/send")
    @SendTo("/topic/chat-rooms/{roomId}")
    public MessageResponse handleMessage(
            @DestinationVariable Long roomId,
            MessageRequest request,
            Principal principal
    ) {
        return messageService.sendMessage(roomId, extractUserId(principal), request);
    }

    private Long extractUserId(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth &&
                auth.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser.getId();
        }
        throw new IllegalStateException("Unauthenticated WebSocket connection");
    }
}
