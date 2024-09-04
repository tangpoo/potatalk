package com.potatalk.chatroomservice.exception;

public class MaxParticipantsExceededException extends RuntimeException {

    public MaxParticipantsExceededException(String message) {
        super(message);
    }
}
