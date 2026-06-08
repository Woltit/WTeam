package com.wteam.backend.message;

import com.wteam.backend.TestcontainersConfiguration;
import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.category.Category;
import com.wteam.backend.category.CategoryRepository;
import com.wteam.backend.chat_room.ChatRoom;
import com.wteam.backend.chat_room.ChatRoomRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("MessageRepository Integration Tests")
class MessageRepositoryTest {

    @Autowired private MessageRepository messageRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    private ChatRoom chatRoom;
    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = User.builder()
                .email("msg-sender-" + System.nanoTime() + "@test.com")
                .password("hashed").role(Role.USER).isActive(true).build();
        receiver = User.builder()
                .email("msg-receiver-" + System.nanoTime() + "@test.com")
                .password("hashed").role(Role.USER).isActive(true).build();
        userRepository.save(sender);
        userRepository.save(receiver);

        Category cat = Category.builder()
                .name("Msg Cat " + System.nanoTime()).slug("msg-cat-" + System.nanoTime()).build();
        categoryRepository.save(cat);

        Item item = Item.builder()
                .owner(receiver).category(cat).title("Camera").description("A camera")
                .condition(ItemCondition.IDEAL).pricePerDay(BigDecimal.valueOf(80))
                .depositAmount(BigDecimal.valueOf(30)).status(RentingStatus.AVAILABLE)
                .city("Lviv").address("2 Test Ave").isVerified(true).build();
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .item(item).renter(sender)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 5))
                .status(BookingStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(320))
                .depositTotal(BigDecimal.valueOf(120))
                .pricePerDaySnapshot(BigDecimal.valueOf(80))
                .build();
        bookingRepository.save(booking);

        chatRoom = ChatRoom.builder().booking(booking).build();
        chatRoomRepository.save(chatRoom);
    }

    @Test
    @DisplayName("findByChatRoom_IdOrderByCreatedAtAsc should return messages in ascending order")
    void findMessages_shouldReturnInAscendingCreatedAtOrder() {
        Message m1 = Message.builder()
                .chatRoom(chatRoom).sender(sender).messageText("First").build();
        Message m2 = Message.builder()
                .chatRoom(chatRoom).sender(receiver).messageText("Second").build();

        messageRepository.save(m1);
        messageRepository.save(m2);
        messageRepository.flush();

        List<Message> messages = messageRepository.findByChatRoom_IdOrderByCreatedAtAsc(chatRoom.getId());

        assertEquals(2, messages.size());
        assertEquals("First",  messages.get(0).getMessageText());
        assertEquals("Second", messages.get(1).getMessageText());
    }

    @Test
    @DisplayName("markAllAsRead should only mark messages from the OTHER user as read")
    void markAllAsRead_shouldOnlyMarkOtherUsersMessages() {
        Message fromSender   = Message.builder()
                .chatRoom(chatRoom).sender(sender).messageText("Hi").isRead(false).build();
        Message fromReceiver = Message.builder()
                .chatRoom(chatRoom).sender(receiver).messageText("Hello").isRead(false).build();
        messageRepository.save(fromSender);
        messageRepository.save(fromReceiver);
        messageRepository.flush();

        // Sender marks room as read (should mark receiver's message, NOT own)
        messageRepository.markAllAsRead(chatRoom.getId(), sender.getId());
        messageRepository.flush();

        List<Message> all = messageRepository.findByChatRoom_IdOrderByCreatedAtAsc(chatRoom.getId());
        Message senderMsg   = all.stream().filter(m -> m.getSender().getId().equals(sender.getId())).findFirst().orElseThrow();
        Message receiverMsg = all.stream().filter(m -> m.getSender().getId().equals(receiver.getId())).findFirst().orElseThrow();

        assertFalse(senderMsg.isRead(),   "Sender's own message should NOT be marked as read");
        assertTrue(receiverMsg.isRead(),  "Receiver's message SHOULD be marked as read by sender");
    }
}
