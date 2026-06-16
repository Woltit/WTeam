package com.wteam.backend.notification;

import com.wteam.backend.notification.dto.NotificationResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Сповіщення", description = "API для роботи зі сповіщеннями користувача (про бронювання, платежі тощо)")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Мої сповіщення", description = "Повертає список сповіщень поточного користувача з пагінацією")
    @GetMapping("/me")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            @CurrentUser UserPrincipalDto user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.getUserNotifications(user.id(), pageable));
    }

    @Operation(summary = "Позначити як прочитане", description = "Змінює статус конкретного сповіщення на 'прочитано'")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @CurrentUser UserPrincipalDto user
    ) {
        notificationService.markAsRead(notificationId, user.id());
        return ResponseEntity.noContent().build();
    }
}
