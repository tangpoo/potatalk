package com.potatalk.chatroomservice.domain;

import com.potatalk.chatroomservice.exception.MaxParticipantsExceededException;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("chat_rooms")
@Getter
public class ChatRoom {

    @Id
    private Long id;

    private Long createMemberId;

    private boolean IsPrivate;

    private String secretKey;

    private Integer maxParticipation;

    private Integer participationCount;

    public boolean matchSecretKey(String secretKey) {
        return this.secretKey.equals(secretKey);
    }

    public void joinParticipation() {
        if (participationCount + 1 > maxParticipation) {
            throw new MaxParticipantsExceededException("채팅방이 최대 인원입니다.");
        }
        this.participationCount++;
    }

}
