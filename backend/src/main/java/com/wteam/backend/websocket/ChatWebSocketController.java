package com.wteam.backend.websocket;

import com.wteam.backend.message.MessageService;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
            @CurrentUser UserPrincipalDto currentUser
    ) {
        return messageService.sendMessage(roomId, currentUser.id(), request);
    }
}
