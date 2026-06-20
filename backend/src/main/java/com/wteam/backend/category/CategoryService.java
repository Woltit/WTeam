package com.wteam.backend.category;

import com.wteam.backend.category.dto.CategoryRequest;
import com.wteam.backend.category.dto.CategoryResponse;
import com.wteam.backend.exception.category.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервіс для керування категоріями товарів.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням, редагуванням та отриманням списку категорій.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryTree")
    public List<CategoryResponse> getCategoryTree() {
        return new java.util.ArrayList<>(categoryMapper.toCategoryTree(categoryRepository.findAll()));
    }

    @Transactional
    @CacheEvict(value = "categoryTree", allEntries = true)
    public CategoryResponse createCategory(final CategoryRequest request) {
        Category parent = null;

        Long parentId = request.parentId();
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new CategoryNotFoundException(parentId));
        }

        Category category = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .iconUrl(request.iconUrl())
                .parent(parent)
                .build();

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categoryTree", allEntries = true)
    public CategoryResponse updateCategory(final Long categoryId, final CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.parentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category.setName(request.name());
        category.setSlug(request.slug());
        category.setIconUrl(request.iconUrl());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categoryTree", allEntries = true)
    public void deleteCategory(final Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }
}
