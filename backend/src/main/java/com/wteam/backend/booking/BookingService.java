package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingRequest;
import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.exception.booking.BookingNotFoundException;
import com.wteam.backend.exception.booking.ItemNotAvailableException;
import com.wteam.backend.exception.item.ItemNotFoundException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервіс для керування бронюваннями.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням, оновленням та отриманням інформації про бронювання товарів.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    public Page<BookingResponse> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Long itemId   = request.itemId();
        Long renterId = request.renterId();

        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User renter = userRepository.findById(renterId)
                .orElseThrow(() -> new UserNotFoundException(renterId));

        boolean isOccupied = bookingRepository.existsOverlappingBooking(
                itemId,
                request.startDate(),
                request.endDate()
        );

        if (isOccupied) {
            throw new ItemNotAvailableException(request.itemId(), request.startDate(), request.endDate());
        }

        Booking booking = Booking.builder()
                .item(item)
                .renter(renter)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        booking.calculatePrices(item.getPricePerDay(), item.getDepositAmount());
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse approveBooking(Long bookingId, Long ownerId) {
        return setStatusForBooking(bookingId, ownerId, BookingStatus.APPROVED, null);
    }

    @Transactional
    public BookingResponse rejectBooking(Long bookingId, Long ownerId) {
        return setStatusForBooking(bookingId, ownerId, BookingStatus.REJECTED, null);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long ownerId, String cancellationReason) {
        return setStatusForBooking(bookingId, ownerId, BookingStatus.CANCELLED, cancellationReason);
    }

    @Transactional
    public BookingResponse completeBooking(Long bookingId, Long ownerId) {
        return setStatusForBooking(bookingId, ownerId, BookingStatus.COMPLETED, null);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findAllByRenterId(Long renterId, Pageable pageable) {
        return bookingRepository.findAllByRenterId(renterId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findAllByOwnerId(Long ownerId, Pageable pageable) {
        return bookingRepository.findAllByOwnerId(ownerId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findActiveBookingsByItemId(Long itemId) {
        return bookingRepository.findByActiveBookingsByItemId(itemId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }


    private BookingResponse setStatusForBooking(Long bookingId, Long ownerId, BookingStatus newStatus, String cancellationReason) {
        Booking booking = getBooking(bookingId);

        boolean isRenter = booking.getRenter().getId().equals(ownerId);
        boolean isOwner  = booking.getItem().getOwner().getId().equals(ownerId);

        if (newStatus == BookingStatus.APPROVED || newStatus == BookingStatus.REJECTED || newStatus == BookingStatus.COMPLETED) {
            if (!isOwner) {
                throw new IllegalArgumentException("Only the owner of the item can perform this action");
            }
        } else if (newStatus == BookingStatus.CANCELLED && !isRenter && !isOwner) {
                throw new IllegalArgumentException("Only the renter or owner can cancel the booking");
        }


        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a cancelled booking");
        }

        booking.setStatus(newStatus);

        if (newStatus == BookingStatus.CANCELLED) {
            Assert.notNull(cancellationReason, "CancellationReason cannot be null");
            booking.setCancellationReason(cancellationReason);
        }

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }


    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}
