package com.wteam.backend.item;

import com.wteam.backend.TestcontainersConfiguration;
import com.wteam.backend.category.Category;
import com.wteam.backend.category.CategoryRepository;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("ItemRepository Integration Tests")
class ItemRepositoryTest {

    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    private User owner;
    private Category category;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .email("item-owner-" + System.nanoTime() + "@test.com")
                .password("hashed")
                .role(Role.USER)
                .isActive(true)
                .build();
        userRepository.save(owner);

        category = Category.builder()
                .name("ItemTest Cat " + System.nanoTime())
                .slug("item-cat-" + System.nanoTime())
                .build();
        categoryRepository.save(category);
    }

    private Item buildItem(RentingStatus status, boolean verified) {
        return Item.builder()
                .owner(owner)
                .category(category)
                .title("Tool " + System.nanoTime())
                .description("A tool")
                .condition(ItemCondition.IDEAL)
                .pricePerDay(BigDecimal.valueOf(50))
                .depositAmount(BigDecimal.valueOf(20))
                .status(status)
                .city("Kyiv")
                .address("1 Test St")
                .isVerified(verified)
                .build();
    }

    @Test
    @DisplayName("findAllByStatusAndIsVerifiedTrue should exclude unverified items")
    void findAllByStatusAndIsVerifiedTrue_shouldExcludeUnverified() {
        itemRepository.save(buildItem(RentingStatus.AVAILABLE, false)); // unverified
        itemRepository.save(buildItem(RentingStatus.AVAILABLE, true));  // verified

        Page<Item> result = itemRepository.findAllByStatusAndIsVerifiedTrue(
                RentingStatus.AVAILABLE, Pageable.unpaged());

        assertTrue(result.getContent().stream().allMatch(Item::isVerified),
                "All returned items must be verified");
    }

    @Test
    @DisplayName("findAllByStatusAndIsVerifiedTrue should exclude items with non-AVAILABLE status")
    void findAllByStatusAndIsVerifiedTrue_shouldExcludeRentedItems() {
        itemRepository.save(buildItem(RentingStatus.RENTED, true));     // rented
        itemRepository.save(buildItem(RentingStatus.AVAILABLE, true));  // available

        Page<Item> result = itemRepository.findAllByStatusAndIsVerifiedTrue(
                RentingStatus.AVAILABLE, Pageable.unpaged());

        assertTrue(result.getContent().stream()
                .allMatch(i -> i.getStatus() == RentingStatus.AVAILABLE),
                "All returned items must be AVAILABLE");
    }
}
