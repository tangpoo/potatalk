package com.potatalk.chatroomservice.exception;

public class ChatRoomNotFound extends RuntimeException {

    public ChatRoomNotFound(final String message) {
        super(message);
    }
}
