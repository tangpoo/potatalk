package com.potatalk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
public class ChatMessageDto {

    @Id private String id;

    private String roomId;

    private String sender;

    private String message;
}
