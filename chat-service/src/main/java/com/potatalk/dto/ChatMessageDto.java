package com.potatalk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageDto {

    private String roomId;

    private String sender;

    private String message;
}
