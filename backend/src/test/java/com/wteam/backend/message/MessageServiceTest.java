package com.wteam.backend.message;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.chat_room.ChatRoom;
import com.wteam.backend.chat_room.ChatRoomService;
import com.wteam.backend.exception.chat.ChatAccessDeniedException;
import com.wteam.backend.item.Item;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService Unit Tests")
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private ChatRoomService chatRoomService;
    @Mock private UserRepository userRepository;
    @InjectMocks private MessageService messageService;

    private ChatRoom buildRoom(Long roomId, Long renterId, Long ownerId) {
        User renter = new User(); renter.setId(renterId); renter.setEmail("renter@test.com");
        User owner  = new User(); owner.setId(ownerId);  owner.setEmail("owner@test.com");
        Item item   = new Item(); item.setOwner(owner);
        Booking b   = new Booking(); b.setRenter(renter); b.setItem(item);
        ChatRoom room = new ChatRoom(); room.setId(roomId); room.setBooking(b);
        return room;
    }

    @Test
    @DisplayName("sendMessage should throw ChatAccessDeniedException when user is not in the room")
    void sendMessage_whenUserNotInRoom_throwsAccessDenied() {
        when(chatRoomService.getAndCheckAccess(1L, 99L))
                .thenThrow(new ChatAccessDeniedException());

        assertThrows(ChatAccessDeniedException.class,
                () -> messageService.sendMessage(1L, 99L, new MessageRequest("hello")));
    }

    @Test
    @DisplayName("sendMessage should save message and return response with sender info")
    void sendMessage_whenUserInRoom_shouldSaveAndReturn() {
        ChatRoom room = buildRoom(1L, 2L, 3L);
        User sender   = new User(); sender.setId(2L); sender.setEmail("renter@test.com");
        MessageRequest req = new MessageRequest("Hello!");

        Message savedMsg = new Message();
        savedMsg.setId(100L);
        savedMsg.setSender(sender);
        savedMsg.setMessageText("Hello!");
        savedMsg.setRead(false);
        savedMsg.setChatRoom(room);

        when(chatRoomService.getAndCheckAccess(1L, 2L)).thenReturn(room);
        when(userRepository.findById(2L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any())).thenReturn(savedMsg);

        MessageResponse response = messageService.sendMessage(1L, 2L, req);

        assertNotNull(response);
        assertEquals(2L, response.senderId());
        assertEquals("Hello!", response.messageText());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("markAsRead should delegate to repository with correct parameters")
    void markAsRead_shouldCallRepositoryMarkAllAsRead() {
        ChatRoom room = buildRoom(1L, 2L, 3L);

        when(chatRoomService.getAndCheckAccess(1L, 2L)).thenReturn(room);

        messageService.markAsRead(1L, 2L);

        verify(messageRepository).markAllAsRead(eq(1L), eq(2L));
    }

    @Test
    @DisplayName("getMessages should throw ChatAccessDeniedException when user is not in the room")
    void getMessages_whenUserNotInRoom_throwsAccessDenied() {
        when(chatRoomService.getAndCheckAccess(1L, 99L))
                .thenThrow(new ChatAccessDeniedException());

        assertThrows(ChatAccessDeniedException.class,
                () -> messageService.getMessages(1L, 99L));
    }

    @Test
    @DisplayName("getMessages should return ordered messages when user is participant")
    void getMessages_whenUserIsParticipant_shouldReturnMessages() {
        ChatRoom room = buildRoom(1L, 2L, 3L);
        User sender   = new User(); sender.setId(2L); sender.setEmail("renter@test.com");
        Message msg   = new Message();
        msg.setId(1L); msg.setSender(sender); msg.setMessageText("hi"); msg.setRead(false);
        msg.setChatRoom(room);

        when(chatRoomService.getAndCheckAccess(1L, 2L)).thenReturn(room);
        when(messageRepository.findByChatRoom_IdOrderByCreatedAtAsc(1L)).thenReturn(List.of(msg));

        List<MessageResponse> result = messageService.getMessages(1L, 2L);

        assertEquals(1, result.size());
        assertEquals("hi", result.getFirst().messageText());
    }
}
