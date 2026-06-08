package com.wteam.backend.booking;

import com.wteam.backend.TestcontainersConfiguration;
import com.wteam.backend.category.Category;
import com.wteam.backend.category.CategoryRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("BookingRepository Integration Tests")
class BookingRepositoryTest {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Item testItem;
    private User renter;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .email("owner-repo@test.com")
                .password("hashed")
                .role(Role.USER)
                .isActive(true)
                .build();
        renter = User.builder()
                .email("renter-repo@test.com")
                .password("hashed")
                .role(Role.USER)
                .isActive(true)
                .build();
        userRepository.save(owner);
        userRepository.save(renter);

        Category category = Category.builder()
                .name("Test Category " + System.nanoTime())
                .slug("test-cat-" + System.nanoTime())
                .build();
        categoryRepository.save(category);

        testItem = Item.builder()
                .owner(owner)
                .category(category)
                .title("Test Drill")
                .description("A test drill")
                .condition(ItemCondition.IDEAL)
                .pricePerDay(BigDecimal.valueOf(100))
                .depositAmount(BigDecimal.valueOf(50))
                .status(RentingStatus.AVAILABLE)
                .city("Kyiv")
                .address("123 Test St")
                .isVerified(true)
                .build();
        itemRepository.save(testItem);

        // Create a base booking for Jul 10–20
        Booking existing = Booking.builder()
                .item(testItem)
                .renter(renter)
                .startDate(LocalDate.of(2026, 7, 10))
                .endDate(LocalDate.of(2026, 7, 20))
                .status(BookingStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(1000))
                .depositTotal(BigDecimal.valueOf(500))
                .pricePerDaySnapshot(BigDecimal.valueOf(100))
                .build();
        bookingRepository.save(existing);
    }

    @Test
    @DisplayName("existsOverlappingBooking returns true when new dates fully contain the existing booking")
    void existsOverlapping_whenFullOverlap_returnsTrue() {
        // Jul 5–25 fully contains Jul 10–20
        assertTrue(bookingRepository.existsOverlappingBooking(
                testItem.getId(),
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 25)
        ));
    }

    @Test
    @DisplayName("existsOverlappingBooking returns true when new dates partially overlap existing")
    void existsOverlapping_whenPartialOverlap_returnsTrue() {
        // Jul 15–25 partially overlaps Jul 10–20
        assertTrue(bookingRepository.existsOverlappingBooking(
                testItem.getId(),
                LocalDate.of(2026, 7, 15),
                LocalDate.of(2026, 7, 25)
        ));
    }

    @Test
    @DisplayName("existsOverlappingBooking returns false when dates are adjacent (no actual overlap)")
    void existsOverlapping_whenAdjacentDates_returnsFalse() {
        // Jul 20–25 starts on the last day of existing → depends on query
        // Query: startDate <= :endDate AND endDate >= :startDate
        // existing: start=10, end=20; new: start=21, end=25
        // 10 <= 25 AND 20 >= 21 → false (20 < 21)
        assertFalse(bookingRepository.existsOverlappingBooking(
                testItem.getId(),
                LocalDate.of(2026, 7, 21),
                LocalDate.of(2026, 7, 25)
        ));
    }

    @Test
    @DisplayName("existsOverlappingBooking returns false for a CANCELLED booking (should not block new bookings)")
    void existsOverlapping_whenExistingIsCancelled_returnsFalse() {
        // Cancel the existing booking
        Booking cancelledBooking = Booking.builder()
                .item(testItem)
                .renter(renter)
                .startDate(LocalDate.of(2026, 8, 10))
                .endDate(LocalDate.of(2026, 8, 20))
                .status(BookingStatus.CANCELLED)
                .cancellationReason("Test cancel")
                .totalPrice(BigDecimal.valueOf(1000))
                .depositTotal(BigDecimal.valueOf(500))
                .pricePerDaySnapshot(BigDecimal.valueOf(100))
                .build();
        bookingRepository.save(cancelledBooking);

        // Same dates as cancelled booking — should be free
        assertFalse(bookingRepository.existsOverlappingBooking(
                testItem.getId(),
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 20)
        ));
    }
}
