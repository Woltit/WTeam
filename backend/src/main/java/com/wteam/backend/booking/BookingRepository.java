package com.wteam.backend.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link Booking}.
 * <p>
 * Забезпечує доступ до даних бронювань у базі даних.
 * </p>
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
