package com.potatalk.chatroomservice.exception;

public class PrivateKeyIsNotMatchedException extends RuntimeException {

    public PrivateKeyIsNotMatchedException(String message) {
        super(message);
    }
}
