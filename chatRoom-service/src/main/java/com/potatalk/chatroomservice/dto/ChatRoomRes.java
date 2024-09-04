package com.potatalk.chatroomservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomRes {

    private Long createMemberId;

    private String chatRoomName;

    private boolean IsPrivate;

    private Integer maxParticipation;

    private Integer participationCount;
}
