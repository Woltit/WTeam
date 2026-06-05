package com.wteam.backend.category;

import com.wteam.backend.category.dto.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public List<CategoryResponse> toCategoryTree(final List<Category> categories) {
        Map<Long, List<Category>> childrenMap = categories.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return categories.stream()
                .filter(c -> c.getParent() == null)
                .map(c -> mapToNode(c, childrenMap))
                .toList();
    }

    public CategoryResponse mapToNode(Category category, final Map<Long, List<Category>> childrenMap) {
        List<CategoryResponse> subcategories = childrenMap.getOrDefault(category.getId(), Collections.emptyList())
                .stream()
                .map(c -> mapToNode(c, childrenMap))
                .toList();

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getIconUrl(),
                category.getParent() != null ? category.getParent().getId() : null,
                subcategories
        );
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getIconUrl(),
                category.getParent() != null ? category.getParent().getId() : null,
                Collections.emptyList()
        );
    }
}
