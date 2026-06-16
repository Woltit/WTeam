package com.wteam.backend.message;

import com.wteam.backend.chat_room.ChatRoom;
import com.wteam.backend.chat_room.ChatRoomService;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.message.dto.MessageRequest;
import com.wteam.backend.message.dto.MessageResponse;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long roomId, Long currentUserId) {
        chatRoomService.getAndCheckAccess(roomId, currentUserId);
        return messageRepository.findByChatRoom_IdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MessageResponse sendMessage(Long roomId, Long currentUserId, MessageRequest request) {
        ChatRoom room = chatRoomService.getAndCheckAccess(roomId, currentUserId);
        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        Message message = Message.builder()
                .chatRoom(room)
                .sender(sender)
                .messageText(request.messageText())
                .build();

        return toResponse(messageRepository.save(message));
    }

    @Transactional
    public void markAsRead(Long roomId, Long currentUserId) {
        chatRoomService.getAndCheckAccess(roomId, currentUserId);
        messageRepository.markAllAsRead(roomId, currentUserId);
    }

    private MessageResponse toResponse(Message message) {
        User sender = message.getSender();
        return new MessageResponse(
                message.getId(),
                sender.getId(),
                sender.getEmail(),
                message.getMessageText(),
                message.isRead(),
                message.getCreatedAt()
        );
    }
}
