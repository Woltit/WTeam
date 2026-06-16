package com.wteam.backend.chat_room;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.chat_room.dto.ChatRoomResponse;
import com.wteam.backend.exception.chat.ChatAccessDeniedException;
import com.wteam.backend.item.Item;
import com.wteam.backend.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatRoomService Unit Tests")
class ChatRoomServiceTest {

    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private BookingRepository bookingRepository;
    @InjectMocks private ChatRoomService chatRoomService;

    private Booking buildBooking(Long renterId, Long ownerId) {
        User renter = new User(); renter.setId(renterId);
        User owner  = new User(); owner.setId(ownerId);
        Item item   = new Item(); item.setId(10L); item.setOwner(owner); item.setTitle("Drill");
        Booking b   = new Booking();
        b.setId(1L);
        b.setRenter(renter);
        b.setItem(item);
        return b;
    }

    @Test
    @DisplayName("getOrCreateByBookingId should return existing room on second call (idempotency)")
    void getOrCreateByBookingId_whenRoomAlreadyExists_shouldReturnSameRoom() {
        Booking booking = buildBooking(2L, 3L);
        ChatRoom existingRoom = new ChatRoom();
        existingRoom.setId(42L);
        existingRoom.setBooking(booking);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(chatRoomRepository.findByBookingId(1L)).thenReturn(Optional.of(existingRoom));

        ChatRoomResponse first  = chatRoomService.getOrCreateByBookingId(1L, 2L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        ChatRoomResponse second = chatRoomService.getOrCreateByBookingId(1L, 2L);

        assertEquals(first.id(), second.id());
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOrCreateByBookingId should throw ChatAccessDeniedException for non-participant")
    void getOrCreateByBookingId_whenUserNotParticipant_throwsAccessDenied() {
        Booking booking = buildBooking(2L, 3L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ChatAccessDeniedException.class,
                () -> chatRoomService.getOrCreateByBookingId(1L, 99L));
    }

    @Test
    @DisplayName("getRoomsForUser should return only rooms for the given user")
    void getRoomsForUser_shouldReturnRoomsFromRepository() {
        Booking booking = buildBooking(2L, 3L);
        ChatRoom room = new ChatRoom();
        room.setId(1L);
        room.setBooking(booking);

        when(chatRoomRepository.findAllByUserId(2L)).thenReturn(List.of(room));

        List<ChatRoomResponse> result = chatRoomService.getRoomsForUser(2L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
    }
}
