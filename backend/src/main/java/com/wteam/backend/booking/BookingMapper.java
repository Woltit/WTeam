package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking is null");
        }

        Long itemId = booking.getItem() != null ? booking.getItem().getId() : null;
        Long renterId = booking.getRenter() != null ? booking.getRenter().getId() : null;

        return new BookingResponse(
                booking.getId(),
                itemId,
                renterId,
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalPrice(),
                booking.getDepositTotal(),
                booking.getPricePerDaySnapshot(),
                booking.getStatus()
        );
    }
}
