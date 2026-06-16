package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.*;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Бронювання", description = "API для управління орендою (бронюваннями) речей")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Отримати всі бронювання", description = "Тільки для адміністраторів. Повертає список усіх бронювань у системі.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> findAllBookings(Pageable pageable) {
        return ResponseEntity.ok(bookingService.findAll(pageable));
    }

    @Operation(summary = "Створити бронювання", description = "Створює запит на оренду речі на вказані дати.")
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @CurrentUser UserPrincipalDto currentUser
    ) {
        BookingRequest bookingRequest = new BookingRequest(
                request.itemId(), currentUser.id(), request.startDate(), request.endDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(bookingRequest));
    }

    @Operation(summary = "Отримати недоступні дати", description = "Повертає список дат, на які річ вже заброньована або недоступна.")
    @GetMapping("/items/{itemId}/unavailable-dates")
    public ResponseEntity<List<UnavailableDateRange>> getUnavailableDates(@PathVariable Long itemId) {
        return ResponseEntity.ok(bookingService.getUnavailableDates(itemId));
    }

    @Operation(summary = "Мої оренди (як орендар)", description = "Повертає список речей, які поточний користувач взяв або хоче взяти в оренду.")
    @GetMapping("/my")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @CurrentUser UserPrincipalDto currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(bookingService.findAllByRenterId(currentUser.id(), pageable));
    }

    @Operation(summary = "Мої здачі в оренду (як власник)", description = "Повертає список запитів від інших користувачів на оренду ваших речей.")
    @GetMapping("/owner")
    public ResponseEntity<Page<BookingResponse>> getOwnerBookings(
            @CurrentUser UserPrincipalDto currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(bookingService.findAllByOwnerId(currentUser.id(), pageable));
    }

    @Operation(summary = "Змінити статус (Адмін)", description = "Тільки для адміністраторів. Дозволяє примусово змінити статус бронювання.")
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

    @Operation(summary = "Схвалити бронювання", description = "Власник речі погоджується надати її в оренду.")
    @PatchMapping("/{bookingId}/approve")
    public ResponseEntity<BookingResponse> approveBooking(
            @PathVariable Long bookingId,
            @CurrentUser UserPrincipalDto currentUser
    ) {
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, currentUser.id()));
    }

    @Operation(summary = "Відхилити бронювання", description = "Власник речі відмовляє в оренді.")
    @PatchMapping("/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long bookingId,
            @CurrentUser UserPrincipalDto currentUser
    ) {
        return ResponseEntity.ok(bookingService.rejectBooking(bookingId, currentUser.id()));
    }

    @Operation(summary = "Скасувати бронювання", description = "Орендар або власник скасовує підтверджене бронювання.")
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) BookingCancelRequest request,
            @CurrentUser UserPrincipalDto currentUser
    ) {
        String reason = request != null && request.cancellationReason() != null
                ? request.cancellationReason()
                : "Cancelled by user";
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, currentUser.id(), reason));
    }

    @Operation(summary = "Завершити оренду", description = "Власник підтверджує, що річ повернуто і оренда успішно завершена.")
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId,
            @CurrentUser UserPrincipalDto currentUser
    ) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId, currentUser.id()));
    }
}
