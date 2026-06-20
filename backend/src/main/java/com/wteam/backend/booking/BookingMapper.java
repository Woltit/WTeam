package com.wteam.backend.booking;

import com.wteam.backend.booking.dto.BookingResponse;
import com.wteam.backend.common.interfaces.Mapper;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper implements Mapper<Void, BookingResponse, Booking> {

    @Override
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
                booking.getStatus(),
                booking.getCancellationReason()
        );
    }

    @Override
    public Booking toEntity(Void dto) {
        return null;
    }
}
