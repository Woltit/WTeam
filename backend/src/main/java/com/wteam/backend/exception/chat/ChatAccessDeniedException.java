package com.wteam.backend.exception.chat;

import com.wteam.backend.exception.base_with_status.ForbiddenOperationException;

public class ChatAccessDeniedException extends ForbiddenOperationException {
    public ChatAccessDeniedException() {
        super("You do not have access to this chat room");
    }
}
