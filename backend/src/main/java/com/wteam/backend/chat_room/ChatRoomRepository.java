package com.wteam.backend.chat_room;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @EntityGraph(attributePaths = {"booking", "booking.item", "booking.renter"})
    Optional<ChatRoom> findByBookingId(Long bookingId);

    @EntityGraph(attributePaths = {"booking", "booking.item", "booking.renter", "booking.item.owner"})
    @Query("""
        SELECT cr FROM ChatRoom cr
        JOIN cr.booking b
        WHERE b.renter.id = :userId OR b.item.owner.id = :userId
        ORDER BY cr.createdAt DESC
    """)
    List<ChatRoom> findAllByUserId(@Param("userId") Long userId);
}
