package com.wteam.backend.chat_room;

import com.wteam.backend.chat_room.dto.ChatRoomResponse;
import com.wteam.backend.message.MessageService;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Чати та повідомлення", description = "API для роботи з чатами")
@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // Відкрити або отримати чат для бронювання
    @Operation(summary = "Отримати або створити чат", description = "Створює або повертає існуючий чат для вказаного бронювання")
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
    @Operation(summary = "Мої чати", description = "Отримання списку всіх чатів поточного користувача")
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        return ResponseEntity.ok(
                chatRoomService.getRoomsForUser(currentUser.getId())
        );
    }

    // Повідомлення кімнати
    @Operation(summary = "Повідомлення чату", description = "Отримання історії повідомлень для конкретної кімнати")
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
    @Operation(summary = "Надіслати повідомлення", description = "Відправка нового повідомлення в чат")
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
    @Operation(summary = "Прочитати повідомлення", description = "Позначити всі повідомлення в кімнаті як прочитані")
    @PatchMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        messageService.markAsRead(roomId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
