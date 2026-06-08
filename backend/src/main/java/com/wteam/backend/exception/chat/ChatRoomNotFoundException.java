package com.wteam.backend.exception.chat;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class ChatRoomNotFoundException extends ResourceNotFoundException {
    public ChatRoomNotFoundException(Long id) {
        super("ChatRoom", id);
    }
}
