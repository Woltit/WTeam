package com.wteam.backend.booking_delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link BookingDelivery}.
 * <p>
 * Забезпечує доступ до даних про доставку бронювань у базі даних.
 * </p>
 */
@Repository
public interface BookingDeliveryRepository extends JpaRepository<BookingDelivery, Long> {
}
