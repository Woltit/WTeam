package com.wteam.backend.message;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"sender"})
    List<Message> findByChatRoom_IdOrderByCreatedAtAsc(Long chatRoomId);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Message m SET m.isRead = true
        WHERE m.chatRoom.id = :roomId
          AND m.sender.id != :userId
          AND m.isRead = false
    """)
    void markAllAsRead(@Param("roomId") Long roomId, @Param("userId") Long userId);
}
