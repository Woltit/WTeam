package com.wteam.backend.item_image.dto;

public record ItemImageResponse(
        Long id,
        String imageUrl,
        boolean isMain
) {}
