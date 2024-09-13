package com.potatalk.domain;

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

    private Long roomId;

    private String sender;

    private String message;

    private Chat(final Long roomId, final String sender, final String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }

    public static Chat create(final Long roomId, final String sender, final String message) {
        return new Chat(roomId, sender, message);
    }
}
