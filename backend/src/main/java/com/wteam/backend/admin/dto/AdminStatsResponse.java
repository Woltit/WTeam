package com.wteam.backend.admin.dto;

import java.util.List;

public record AdminStatsResponse(
        long totalUsers,
        long activeBookings,
        long completedBookings,
        long totalItems,
        List<CategoryStatDto> topCategories
) {}
