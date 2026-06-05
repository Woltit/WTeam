package com.wteam.backend.item;

import com.wteam.backend.category.Category;
import com.wteam.backend.category.CategoryRepository;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.exception.category.CategoryNotFoundException;
import com.wteam.backend.exception.item.ItemNotFoundException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.item.dto.ItemRequest;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService Unit Tests")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ItemService itemService;

    private ItemRequest createDummyRequest(Long categoryId) {
        return new ItemRequest(
                categoryId,
                "Item Name",
                "Description",
                List.of("tag1"),
                ItemCondition.IDEAL,
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(100.0),
                "City",
                "Address",
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(30.0)
        );
    }

    @Test
    @DisplayName("getAllItems should return page of item responses")
    void getAllItems_shouldReturnPage() {
        Item item = new Item();
        ItemResponse response = mock(ItemResponse.class);
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        Pageable pageable = Pageable.unpaged();

        when(itemRepository.findAll(pageable)).thenReturn(itemsPage);
        when(itemMapper.toItemResponse(item)).thenReturn(response);

        Page<ItemResponse> result = itemService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().getFirst());
    }

    @Test
    @DisplayName("getAllItemsWhichAreAvailable should query repository and map")
    void getAllItemsWhichAreAvailable_shouldReturnPage() {
        Item item = new Item();
        ItemResponse response = mock(ItemResponse.class);
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        Pageable pageable = Pageable.unpaged();

        when(itemRepository.findAllByStatusAndIsVerifiedTrue(RentingStatus.AVAILABLE, pageable)).thenReturn(itemsPage);
        when(itemMapper.toItemResponse(item)).thenReturn(response);

        Page<ItemResponse> result = itemService.getAllItemsWhichAreAvailable(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().getFirst());
    }

    @Test
    @DisplayName("getItemById should return response when item exists")
    void getItemById_whenExists_shouldReturnResponse() {
        Long itemId = 1L;
        Item item = new Item();
        ItemResponse response = mock(ItemResponse.class);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemResponse(item)).thenReturn(response);

        ItemResponse result = itemService.getItemById(itemId);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    @DisplayName("getItemById should throw ItemNotFoundException when item does not exist")
    void getItemById_whenDoesNotExist_shouldThrowException() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(itemId));
    }

    @Test
    @DisplayName("createItem should validate profile, retrieve owner and category, then save item")
    void createItem_shouldValidateAndSave() {
        Long userId = 1L;
        ItemRequest request = createDummyRequest(2L);
        User owner = new User();
        Category category = new Category();
        ItemResponse response = mock(ItemResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);
        when(itemMapper.toItemResponse(any(Item.class))).thenReturn(response);

        ItemResponse result = itemService.createItem(userId, request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(userProfileService).validateUserCanPlaceOffers(userId);
        verify(itemMapper).updateItemFromRequest(any(Item.class), eq(request));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("createItem should throw UserNotFoundException when owner not found")
    void createItem_whenOwnerNotFound_shouldThrowException() {
        Long userId = 1L;
        ItemRequest request = createDummyRequest(2L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.createItem(userId, request));
    }

    @Test
    @DisplayName("createItem should throw CategoryNotFoundException when category not found")
    void createItem_whenCategoryNotFound_shouldThrowException() {
        Long userId = 1L;
        ItemRequest request = createDummyRequest(2L);
        User owner = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> itemService.createItem(userId, request));
    }

    @Test
    @DisplayName("updateItem should save modified item if user is owner")
    void updateItem_whenUserIsOwner_shouldUpdateAndSave() {
        Long itemId = 1L;
        Long userId = 2L;
        ItemRequest request = createDummyRequest(3L);

        User owner = new User();
        owner.setId(userId);

        Category category = new Category();
        category.setId(3L);

        Item item = new Item();
        item.setOwner(owner);
        item.setCategory(category);

        ItemResponse response = mock(ItemResponse.class);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemResponse(item)).thenReturn(response);

        ItemResponse result = itemService.updateItem(itemId, userId, request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(itemMapper).updateItemFromRequest(item, request);
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("updateItem should throw IllegalArgumentException if user is not owner")
    void updateItem_whenUserNotOwner_shouldThrowException() {
        Long itemId = 1L;
        Long userId = 2L;
        ItemRequest request = createDummyRequest(3L);

        User owner = new User();
        owner.setId(99L); // different owner

        Item item = new Item();
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(itemId, userId, request));
    }

    @Test
    @DisplayName("deleteItem should archive item if user is owner")
    void deleteItem_whenUserIsOwner_shouldArchive() {
        Long itemId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setOwner(owner);
        item.setStatus(RentingStatus.AVAILABLE);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId, userId);

        assertEquals(RentingStatus.ARCHIVED, item.getStatus());
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("deleteItem should throw IllegalArgumentException if user is not owner")
    void deleteItem_whenUserNotOwner_shouldThrowException() {
        Long itemId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(99L); // different owner

        Item item = new Item();
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(itemId, userId));
    }
}
