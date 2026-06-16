package com.wteam.backend.admin;

import com.wteam.backend.admin.dto.AdminStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Панель адміністратора", description = "API для адміністративних функцій та статистики")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Статистика", description = "Отримання загальної статистики платформи (к-сть користувачів, бронювань, тощо). Доступно для Admin та Moder.")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODER')")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
