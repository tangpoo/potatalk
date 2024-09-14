package com.potatalk.domain;

import com.potatalk.dto.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
public class Chat {

    @Id
    private String id;

    private String roomId;

    private String sender;

    private String message;

    private Chat(ChatMessageDto messageDto) {
        this.roomId = messageDto.getRoomId();
        this.sender = messageDto.getSender();
        this.message = messageDto.getMessage();
    }

    public static Chat create(ChatMessageDto messageDto) {
        return new Chat(messageDto);
    }
}
