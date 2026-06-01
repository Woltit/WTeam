package com.wteam.backend.user;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import com.wteam.backend.user.dto.BlockUserRequest;
import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // ADMIN endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Role role,
            Pageable pageable
    ) {
        Page<UserResponse> users = userService.getAllUsers(isActive, role, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(@PathVariable Long id) {
        userService.activateUser(id);
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(
            @PathVariable Long id,
            @Valid @RequestBody BlockUserRequest request,
            @CurrentUser UserPrincipalDto admin
    ) {
        Long adminId = admin.id();
        userService.deactivateUser(id, adminId, request.reason());
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

    // USER
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMyInfo(
            @CurrentUser UserPrincipalDto user
    ) {
        return ResponseEntity.ok(userService.getUserById(user.id()));
    }
}
