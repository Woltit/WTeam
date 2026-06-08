package com.wteam.backend.chat_room;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.chat_room.dto.ChatRoomResponse;
import com.wteam.backend.exception.booking.BookingNotFoundException;
import com.wteam.backend.exception.chat.ChatAccessDeniedException;
import com.wteam.backend.exception.chat.ChatRoomNotFoundException;
import com.wteam.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public ChatRoomResponse getOrCreateByBookingId(Long bookingId, Long currentUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        checkAccess(booking, currentUserId);

        ChatRoom room = chatRoomRepository.findByBookingId(bookingId)
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .booking(booking)
                            .build();
                    return chatRoomRepository.save(newRoom);
                });

        return toResponse(room, currentUserId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getRoomsForUser(Long currentUserId) {
        return chatRoomRepository.findAllByUserId(currentUserId)
                .stream()
                .map(room -> toResponse(room, currentUserId))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatRoom getAndCheckAccess(Long roomId, Long currentUserId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId));
        checkAccess(room.getBooking(), currentUserId);
        return room;
    }

    private void checkAccess(Booking booking, Long userId) {
        boolean isRenter = booking.getRenter().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        if (!isRenter && !isOwner) {
            throw new ChatAccessDeniedException();
        }
    }

    private ChatRoomResponse toResponse(ChatRoom room, Long currentUserId) {
        Booking booking = room.getBooking();
        User renter = booking.getRenter();
        User owner = booking.getItem().getOwner();

        User otherUser = renter.getId().equals(currentUserId) ? owner : renter;

        String otherUserName = otherUser.getId() + "";
        // якщо є профіль — використати ім'я (profile підтягується окремо при потребі)

        return new ChatRoomResponse(
                room.getId(),
                booking.getId(),
                booking.getItem().getTitle(),
                otherUser.getId(),
                otherUser.getEmail(),
                room.getCreatedAt()
        );
    }
}
