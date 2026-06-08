package com.wteam.backend.chat_room;

import com.wteam.backend.chat_room.dto.ChatRoomResponse;
import com.wteam.backend.message.MessageService;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // Відкрити або отримати чат для бронювання
    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<ChatRoomResponse> getOrCreate(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return ResponseEntity.ok(
                chatRoomService.getOrCreateByBookingId(bookingId, currentUser.getId())
        );
    }

    // Всі мої чати
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return ResponseEntity.ok(
                chatRoomService.getRoomsForUser(currentUser.getId())
        );
    }

    // Повідомлення кімнати
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return ResponseEntity.ok(
                messageService.getMessages(roomId, currentUser.getId())
        );
    }

    // Надіслати повідомлення
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long roomId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                messageService.sendMessage(roomId, currentUser.getId(), request)
        );
    }

    // Позначити повідомлення прочитаними
    @PatchMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        messageService.markAsRead(roomId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
