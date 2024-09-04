package com.potatalk.chatroomservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateChatRoomDto {

    private Long memberId;

    private String chatRoomName;

    private boolean isPrivate;

    private String secretKey;

    private Integer maxParticipation;
}
