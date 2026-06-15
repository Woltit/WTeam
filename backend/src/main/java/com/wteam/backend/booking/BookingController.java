package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingRequest;
import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.booking.dto.BookingStatusUpdateRequest;
import com.wteam.backend.booking.dto.UnavailableDateRange;
import com.wteam.backend.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> findAllBookings(Pageable pageable) {
        return ResponseEntity.ok(bookingService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody UserBookingRequest request,
            @AuthenticationPrincipal SecurityUser currentUser
    ) {
        BookingRequest bookingRequest = new BookingRequest(
                request.itemId(), currentUser.getId(), request.startDate(), request.endDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(bookingRequest));
    }

    @GetMapping("/items/{itemId}/unavailable-dates")
    public ResponseEntity<List<UnavailableDateRange>> getUnavailableDates(@PathVariable Long itemId) {
        return ResponseEntity.ok(bookingService.getUnavailableDates(itemId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal SecurityUser currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(bookingService.findAllByRenterId(currentUser.getId(), pageable));
    }

    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(bookingService.adminUpdateStatus(
                bookingId,
                request.status(),
                request.cancellationReason()
        ));
    }

    record UserBookingRequest(
            Long itemId,
            LocalDate startDate,
            LocalDate endDate
    ) {}
}
