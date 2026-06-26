package com.wteam.backend.item;

import com.wteam.backend.category.Category;
import com.wteam.backend.category.CategoryRepository;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.exception.category.CategoryNotFoundException;
import com.wteam.backend.exception.item.ItemNotFoundException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.item.dto.ItemRequest;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для керування товарами.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням, оновленням, видаленням та пошуком товарів.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ItemResponse> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ItemResponse> getAllItemsWhichAreAvailable(Pageable pageable) {
        return itemRepository.findAllByStatusAndIsVerifiedTrue(RentingStatus.AVAILABLE, pageable)
                .map(itemMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ItemResponse> getMyItems(Long userId, Pageable pageable) {
        return itemRepository.findAllByOwnerId(userId, pageable)
                .map(itemMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "items", key = "#itemId")
    public ItemResponse getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::toResponse)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Transactional
    public ItemResponse createItem(Long userId, ItemRequest request) {
        userProfileService.validateUserCanPlaceOffers(userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Item item = Item.builder()
                .owner(owner)
                .category(category)
                .status(RentingStatus.AVAILABLE)
                .isVerified(false)
                .build();

        itemMapper.updateItemFromRequest(item, request);

        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Transactional
    @CacheEvict(value = "items", key = "#itemId")
    public ItemResponse updateItem(Long itemId, Long userId, ItemRequest request) {
        Item item = getItem(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this item");
        }

        if (!item.getCategory().getId().equals(request.categoryId())) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
            item.setCategory(category);
        }

        itemMapper.updateItemFromRequest(item, request);
        return itemMapper.toResponse(itemRepository.save(item));
    }

    @Transactional
    @CacheEvict(value = "items", key = "#itemId")
    public void deleteItem(Long itemId, Long userId, boolean isAdmin) {
        Item item = getItem(itemId);

        if (!isAdmin && !item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not the owner of this item");
        }

        item.setStatus(RentingStatus.ARCHIVED);
        itemRepository.save(item);
    }

    @Transactional
    @CacheEvict(value = "items", key = "#itemId")
    public ItemResponse setItemVerified(Long itemId, boolean verified) {
        Item item = getItem(itemId);
        item.setVerified(verified);
        return itemMapper.toResponse(itemRepository.save(item));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }
}
