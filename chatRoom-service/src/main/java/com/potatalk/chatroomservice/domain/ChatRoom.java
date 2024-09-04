package com.potatalk.chatroomservice.domain;

import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
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

    private String chatRoomName;

    private boolean IsPrivate;

    private String secretKey;

    private Integer maxParticipation;

    private Integer participationCount;

    public ChatRoom(CreateChatRoomDto createChatRoomDto) {
        this.createMemberId = createChatRoomDto.getMemberId();
        this.chatRoomName = createChatRoomDto.getChatRoomName();
        IsPrivate = createChatRoomDto.isPrivate();
        this.secretKey = createChatRoomDto.getSecretKey();
        this.maxParticipation = createChatRoomDto.getMaxParticipation();
        this.participationCount = 0;
    }

    public static ChatRoom create(final CreateChatRoomDto createChatRoomDto) {
        return new ChatRoom(createChatRoomDto);
    }

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
