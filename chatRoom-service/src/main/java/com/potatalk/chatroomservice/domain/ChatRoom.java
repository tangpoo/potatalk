package com.potatalk.chatroomservice.domain;

import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.exception.MaxParticipantsExceededException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    private Long id;

    private Long createMemberId;

    private String roomName;

    private Boolean isPrivate;

    private String secretKey;

    private ChatRoomStatus chatRoomStatus;

    private Integer maxParticipation;

    private Integer participationCount;

    public ChatRoom(CreateChatRoomDto createChatRoomDto, ChatRoomStatus chatRoomStatus) {
        this.createMemberId = createChatRoomDto.getMemberId();
        this.roomName = createChatRoomDto.getChatRoomName();
        this.isPrivate = createChatRoomDto.isPrivate();
        this.secretKey = createChatRoomDto.getSecretKey();
        this.chatRoomStatus = chatRoomStatus;
        this.maxParticipation = createChatRoomDto.getMaxParticipation();
        this.participationCount = 0;
    }

    public static ChatRoom create(final CreateChatRoomDto createChatRoomDto,
        ChatRoomStatus chatRoomStatus) {
        return new ChatRoom(createChatRoomDto, chatRoomStatus);
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

    public boolean canInviteParticipation() {
        return participationCount < maxParticipation;
    }
}
