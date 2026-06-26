package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingRequest;
import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.booking.dto.UnavailableDateRange;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.common.enums.NotificationType;
import com.wteam.backend.exception.booking.BookingNotFoundException;
import com.wteam.backend.exception.booking.InvalidBookingStateException;
import com.wteam.backend.exception.booking.ItemNotAvailableException;
import com.wteam.backend.exception.item.ItemNotFoundException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.notification.dto.NotificationEvent;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectProvider<BookingService> selfProvider;

    private BookingService getSelf() {
        return selfProvider.getObject();
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Booking findById(final Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Transactional
    @CacheEvict(value = "unavailableDates", key = "#request.itemId()")
    public BookingResponse createBooking(final BookingRequest request, final Long renterId) {
        Long itemId = request.itemId();

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

        Booking savedBooking = bookingRepository.save(booking);
        BookingResponse response = bookingMapper.toResponse(savedBooking);

        sendNotification(savedBooking, item.getOwner().getId(), NotificationType.BOOKING_REQUEST);

        return response;
    }

    @Transactional
    public BookingResponse approveBooking(Long bookingId, Long userId) {
        return getSelf().setStatusForBooking(bookingId, userId, BookingStatus.APPROVED, null);
    }

    @Transactional
    public BookingResponse rejectBooking(Long bookingId, Long userId) {
        return getSelf().setStatusForBooking(bookingId, userId, BookingStatus.REJECTED, null);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId, String cancellationReason) {
        return getSelf().setStatusForBooking(bookingId, userId, BookingStatus.CANCELLED, cancellationReason);
    }

    @Transactional
    public BookingResponse completeBooking(Long bookingId, Long userId) {
        return getSelf().setStatusForBooking(bookingId, userId, BookingStatus.COMPLETED, null);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findAllByRenterId(Long renterId, Pageable pageable) {
        return bookingRepository.findAllByRenterId(renterId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findAllByOwnerId(Long userId, Pageable pageable) {
        return bookingRepository.findAllByOwnerId(userId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findActiveBookingsByItemId(Long itemId) {
        return bookingRepository.findByActiveBookingsByItemId(itemId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "unavailableDates", key = "#itemId")
    public List<UnavailableDateRange> getUnavailableDates(Long itemId) {
        return new ArrayList<>(bookingRepository.findByActiveBookingsByItemId(itemId)
                .stream()
                .map(b -> new UnavailableDateRange(b.getStartDate(), b.getEndDate()))
                .toList());
    }

    @Transactional
    @CacheEvict(value = "unavailableDates", key = "#result.itemId")
    public BookingResponse setStatusForBooking(Long bookingId, Long userId, BookingStatus newStatus, String cancellationReason) {
        Booking booking = getBooking(bookingId);

        boolean isRenter = isIsRenter(userId, newStatus, booking);


        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateException("Cannot change status of a cancelled booking");
        }

        booking.setStatus(newStatus);

        if (newStatus == BookingStatus.CANCELLED) {
            Assert.notNull(cancellationReason, "CancellationReason cannot be null");
            booking.setCancellationReason(cancellationReason);
        }

        NotificationType type = switch (newStatus) {
            case APPROVED -> NotificationType.BOOKING_APPROVED;
            case REJECTED -> NotificationType.BOOKING_REJECTED;
            case CANCELLED -> NotificationType.BOOKING_CANCELLED;
            default -> null;
        };

        Long recipientId = isRenter
                ? booking.getItem().getOwner().getId()
                : booking.getRenter().getId();

        if (type != null) {
            sendNotification(booking, recipientId, type);
        }

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }


    @Transactional
    @CacheEvict(value = "unavailableDates", key = "#result.itemId")
    public BookingResponse adminUpdateStatus(Long bookingId, BookingStatus newStatus, String cancellationReason) {
        Booking booking = getBooking(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateException("Cannot change status of a cancelled booking");
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

    private void sendNotification(Booking booking, Long recipientId, NotificationType notificationType) {
        Map<String, Object> payload = Map.of(
                "bookingId", booking.getId(),
                "itemName", booking.getItem().getTitle(),
                "renterName", booking.getRenter().getUserProfile().getFirstName(),
                "reason", booking.getCancellationReason() != null ? booking.getCancellationReason() : ""
        );

        NotificationEvent notificationEvent = new NotificationEvent(recipientId, notificationType, NotificationChannel.IN_APP, payload);

        eventPublisher.publishEvent(notificationEvent);
    }

    private static boolean isIsRenter(Long userId, BookingStatus newStatus, Booking booking) {
        boolean isRenter = booking.getRenter().getId().equals(userId);
        boolean isOwner  = booking.getItem().getOwner().getId().equals(userId);

        if (newStatus == BookingStatus.APPROVED || newStatus == BookingStatus.REJECTED || newStatus == BookingStatus.COMPLETED) {
            if (!isOwner) {
                throw new IllegalArgumentException("Only the owner of the item can perform this action");
            }
        } else if (newStatus == BookingStatus.CANCELLED && !isRenter && !isOwner) {
            throw new IllegalArgumentException("Only the renter or owner can cancel the booking");
        }
        return isRenter;
    }
}
