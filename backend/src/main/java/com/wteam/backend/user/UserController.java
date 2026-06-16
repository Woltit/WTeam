package com.wteam.backend.user;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import com.wteam.backend.user.dto.BlockUserRequest;
import com.wteam.backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Користувачі", description = "API для управління користувачами системи (переважно для адміністраторів)")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // ADMIN endpoints
    @Operation(summary = "Всі користувачі (Адмін)", description = "Отримує список усіх користувачів з можливістю фільтрації")
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

    @Operation(summary = "Користувач за ID (Адмін)", description = "Отримує інформацію про конкретного користувача за його ідентифікатором")
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Пошук користувача (Адмін)", description = "Пошук користувача за email адресою")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @Operation(summary = "Активувати користувача (Адмін)", description = "Знімає блокування з акаунта користувача")
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
    }

    @Operation(summary = "Заблокувати користувача (Адмін)", description = "Блокує акаунт користувача з вказанням причини")
    @PostMapping("/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(
            @PathVariable Long userId,
            @Valid @RequestBody BlockUserRequest request,
            @CurrentUser UserPrincipalDto admin
    ) {
        Long adminId = admin.id();
        userService.deactivateUser(userId, adminId, request.reason());
    }

    @Operation(summary = "Змінити роль (Адмін)", description = "Змінює роль (права доступу) користувача")
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateRole(
            @PathVariable Long userId,
            @RequestParam Role role
    ) {
        userService.updateRole(role, userId);
    }

    @Operation(summary = "Видалити користувача (Адмін)", description = "Повністю видаляє користувача з системи")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

    // USER
    @Operation(summary = "Мій профіль", description = "Отримує дані про поточного автентифікованого користувача")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMyInfo(
            @CurrentUser UserPrincipalDto user
    ) {
        return ResponseEntity.ok(userService.getUserById(user.id()));
    }
}
