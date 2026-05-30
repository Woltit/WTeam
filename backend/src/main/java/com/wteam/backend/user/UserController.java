package com.wteam.backend.user;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.user.dto.BlockUserRequest;
import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    private ResponseEntity<Page<UserResponse>> getAllUsersWhoIsActiveOrNot(Pageable pageable, boolean isActive) {
        if (isActive) {
            return ResponseEntity.ok(userService.getAllUsersWhoIsActive(pageable));
        }
        return ResponseEntity.ok(userService.getAllUsersWhoIsNotActive(pageable));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsersWhoIsActive(Pageable pageable) {
        return getAllUsersWhoIsActiveOrNot(pageable, true);
    }

    @GetMapping("/no-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsersWhoIsNotActive(Pageable pageable) {
        return getAllUsersWhoIsActiveOrNot(pageable, false);
    }

    @GetMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsersByRole(
            @RequestParam Role role,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsersByRole(role, pageable));
    }


    @PostMapping("/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(@PathVariable Long id) {
        userService.activateUser(id);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateRole(
            @PathVariable Long id,
            @RequestParam Role role
    ) {
        userService.updateRole(role, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    /**
     * Block user.
     *
     * @param userId  the user id
     * @param request the request
     * @param admin   the admin
     */
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

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
}
