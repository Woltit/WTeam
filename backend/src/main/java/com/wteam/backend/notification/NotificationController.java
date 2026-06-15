package com.wteam.backend.notification;

import com.wteam.backend.notification.dto.NotificationResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            @CurrentUser UserPrincipalDto user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.getUserNotifications(user.id(), pageable));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @CurrentUser UserPrincipalDto user
    ) {
        notificationService.markAsRead(notificationId, user.id());
        return ResponseEntity.noContent().build();
    }
}
