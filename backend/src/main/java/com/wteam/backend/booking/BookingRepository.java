package com.wteam.backend.booking;

import com.wteam.backend.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторій для роботи з сутностями {@link Booking}.
 * <p>
 * Забезпечує доступ до даних бронювань у базі даних.
 * </p>
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.item.id = :itemId 
        AND b.status NOT IN ('CANCELLED', 'REJECTED') 
        AND b.startDate <= :endDate 
        AND b.endDate >= :startDate
    """)
    boolean existsOverlappingBooking(
            @Param("itemId") Long itemId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @EntityGraph(attributePaths = {"item", "renter"})
    Page<Booking> findAllByRenterId(Long renterId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    @EntityGraph(attributePaths = {"item", "renter"})
    Page<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query("""
        SELECT b FROM Booking b 
        WHERE b.item.id = :itemId 
        AND b.status NOT IN ('CANCELLED', 'REJECTED', 'COMPLETED')
    """)
    List<Booking> findByActiveBookingsByItemId(@Param("itemId") Long itemId);

    Long item(Item item);
}
