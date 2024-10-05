package com.potatalk.steps;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;

public class ChatRoomSteps {

    public static CreateChatRoomDto createGroupChatRoomDto(Boolean isPrivate, String secretKey) {
        return new CreateChatRoomDto(1L, null, "chatRoomName-1234", isPrivate, secretKey, 3);
    }

    public static CreateChatRoomDto createOneToOneChatRoomDto() {
        return new CreateChatRoomDto(1L, 2L, "chatRoomName-1234", false, "secretKey-1234", 2);
    }

    public static ChatRoom createChatRoom(
            CreateChatRoomDto createChatRoomDto, ChatRoomStatus chatRoomStatus) {
        return ChatRoom.create(createChatRoomDto, chatRoomStatus);
    }

    public static ChatRoom createChatRoom() {
        return ChatRoom.create(createGroupChatRoomDto(false, null), ChatRoomStatus.GROUP);
    }

    public static ChatRoom createPrivateChatRoom() {
        return ChatRoom.create(
                createGroupChatRoomDto(true, "secretKey-1234"), ChatRoomStatus.GROUP);
    }

    public static CreateChatRoomDto createFullParticipationChatRoomDto() {
        return new CreateChatRoomDto(1L, null, "chatRoomName-1234", false, null, 0);
    }

    public static ChatRoom createFullParticipationChatRoom() {
        return ChatRoom.create(createFullParticipationChatRoomDto(), ChatRoomStatus.GROUP);
    }
}
