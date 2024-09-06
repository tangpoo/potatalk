package com.potatalk.chatroomservice.exception;

import com.potatalk.chatroomservice.domain.ChatRoom;
import reactor.core.publisher.Mono;

public class ChatRoomNotFound extends RuntimeException {

    public ChatRoomNotFound(final String message) {
        super(message);
    }
}
