package com.wteam.backend.user;

import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.user.dto.BlockUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/block/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(
            @PathVariable Long userId,
            @Valid @RequestBody BlockUserRequest request,
            @AuthenticationPrincipal SecurityUser admin
            ) {
        Long adminId = admin.getId();
        userService.deactivateUser(userId, adminId, request.reason());
    }
}
