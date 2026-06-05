package com.wteam.backend.category.dto;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String iconUrl,
        Long parentId,
        List<CategoryResponse> subcategories
) {}
