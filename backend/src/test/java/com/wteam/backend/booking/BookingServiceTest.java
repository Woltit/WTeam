package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingRequest;
import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.exception.booking.ItemNotAvailableException;
import com.wteam.backend.exception.item.ItemNotFoundException;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private BookingService bookingService;

    private static final LocalDate START = LocalDate.of(2026, 8, 1);
    private static final LocalDate END   = LocalDate.of(2026, 8, 6);

    private User user(Long id) {
        User u = new User();
        u.setId(id);

        UserProfile profile = new UserProfile();
        profile.setFirstName("TestName");
        u.setUserProfile(profile);

        return u;
    }

    private Item item(Long id, User owner, BigDecimal price, BigDecimal deposit) {
        Item i = new Item();
        i.setId(id);
        i.setOwner(owner);
        i.setPricePerDay(price);
        i.setDepositAmount(deposit);
        i.setTitle("Test Item");
        return i;
    }

    private Booking bookingWith(Long bookingId, User renter, Item item, BookingStatus status) {
        Booking b = new Booking();
        b.setId(bookingId);
        b.setRenter(renter);
        b.setItem(item);
        b.setStatus(status);
        b.setStartDate(START);
        b.setEndDate(END);
        return b;
    }

    @Test
    @DisplayName("createBooking should throw ItemNotAvailableException when dates overlap")
    void createBooking_whenDatesOverlap_throwsItemNotAvailableException() {
        User owner = user(3L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);

        when(itemRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
        when(bookingRepository.existsOverlappingBooking(1L, START, END)).thenReturn(true);

        BookingRequest request = new BookingRequest(1L, 2L, START, END);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking should throw ItemNotFoundException when item does not exist")
    void createBooking_whenItemNotFound_throwsItemNotFoundException() {
        when(itemRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        BookingRequest request = new BookingRequest(99L, 2L, START, END);

        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(request));
    }

    @Test
    @DisplayName("createBooking should calculate totalPrice as days × pricePerDay")
    void createBooking_shouldCalculateTotalPriceCorrectly() {
        User owner = user(3L);
        Item item = item(1L, owner, BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        BookingResponse mockResponse = mock(BookingResponse.class);

        when(itemRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
        when(bookingRepository.existsOverlappingBooking(1L, START, END)).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(mockResponse);

        bookingService.createBooking(new BookingRequest(1L, 2L, START, END));

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking captured = captor.getValue();
        assertThat(captured.getTotalPrice()).isEqualByComparingTo(new BigDecimal("600"));
        assertThat(captured.getDepositTotal()).isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    @DisplayName("approveBooking should throw IllegalArgumentException when caller is not item owner")
    void approveBooking_whenCallerIsNotOwner_throwsIllegalArgument() {
        User owner = user(3L);
        User renter = user(2L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);
        Booking booking = bookingWith(10L, renter, item, BookingStatus.PENDING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approveBooking(10L, 99L));
    }

    @Test
    @DisplayName("rejectBooking should throw IllegalArgumentException when caller is not item owner")
    void rejectBooking_whenCallerIsNotOwner_throwsIllegalArgument() {
        User owner = user(3L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);
        Booking booking = bookingWith(10L, user(2L), item, BookingStatus.PENDING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.rejectBooking(10L, 99L));
    }

    @Test
    @DisplayName("cancelBooking should succeed when caller is the renter")
    void cancelBooking_whenCallerIsRenter_shouldSucceed() {
        User owner = user(3L);
        User renter = user(2L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);
        Booking booking = bookingWith(10L, renter, item, BookingStatus.PENDING);
        BookingResponse mockResponse = mock(BookingResponse.class);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(mockResponse);

        BookingResponse result = bookingService.cancelBooking(10L, 2L, "changed mind");

        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("cancelBooking should throw IllegalArgumentException when caller is unrelated third party")
    void cancelBooking_whenCallerIsThirdParty_throwsIllegalArgument() {
        User owner = user(3L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);
        Booking booking = bookingWith(10L, user(2L), item, BookingStatus.PENDING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.cancelBooking(10L, 99L, "reason"));
    }

    @Test
    @DisplayName("cancelBooking should throw IllegalStateException when booking is already CANCELLED")
    void cancelBooking_whenAlreadyCancelled_throwsIllegalState() {
        User owner = user(3L);
        User renter = user(2L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);
        Booking booking = bookingWith(10L, renter, item, BookingStatus.CANCELLED);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class,
                () -> bookingService.cancelBooking(10L, 2L, "reason"));
    }

    @Test
    @DisplayName("completeBooking should throw IllegalArgumentException when caller is not item owner")
    void completeBooking_whenCallerIsNotOwner_throwsIllegalArgument() {
        User owner = user(3L);
        Item item = item(1L, owner, BigDecimal.TEN, BigDecimal.ONE);
        Booking booking = bookingWith(10L, user(2L), item, BookingStatus.APPROVED);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.completeBooking(10L, 99L));
    }
}
