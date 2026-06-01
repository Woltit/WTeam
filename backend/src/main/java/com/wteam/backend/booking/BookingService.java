package com.wteam.backend.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
