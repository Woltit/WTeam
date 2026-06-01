package com.wteam.backend.booking_delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування доставкою бронювань.
 * <p>
 * Обробляє бізнес-логіку, пов'язану з відстеженням та оновленням статусу доставки орендованих товарів.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BookingDeliveryService {
    private final BookingDeliveryRepository bookingDeliveryRepository;
}
