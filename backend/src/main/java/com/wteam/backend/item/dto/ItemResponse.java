package com.wteam.backend.item.dto;

import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.user_profile.dto.PublicProfileResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.wteam.backend.item_image.dto.ItemImageResponse;

public record ItemResponse(
        Long id,
        Long ownerId,
        PublicProfileResponse ownerProfile,
        Long categoryId,
        String title,
        String description,
        List<String> tags,
        ItemCondition condition,
        BigDecimal pricePerDay,
        BigDecimal pricePerWeek,
        BigDecimal depositAmount,
        RentingStatus status,
        String city,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean isVerified,
        Instant createdAt,
        Instant updatedAt,
        List<ItemImageResponse> images
) {}
