package com.potatalk.exception;

public class MessageSendException extends RuntimeException {

    public MessageSendException() {
        super("메시지 전송에 실패했습니다.");
    }
}
