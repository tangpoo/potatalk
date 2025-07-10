package com.potatalk.chatroomservice.dto;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomInfoRes {

    private Long id;

    private Long createMemberId;

    private String roomName;

    private Boolean isPrivate;

    private ChatRoomStatus chatRoomStatus;

    private Integer maxParticipation;

    private Integer participationCount;

    private List<Long> participationIds;

    public static ChatRoomInfoRes from(ChatRoom chatRoom, List<Long> participationIds) {
        return new ChatRoomInfoRes(
                chatRoom.getId(),
                chatRoom.getCreateMemberId(),
                chatRoom.getRoomName(),
                chatRoom.getIsPrivate(),
                chatRoom.getChatRoomStatus(),
                chatRoom.getMaxParticipation(),
                chatRoom.getParticipationCount(),
                participationIds);
    }
}
