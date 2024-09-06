package com.potatalk.steps;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;

public class ChatRoomSteps {
    public static CreateChatRoomDto createGroupChatRoomDto() {
        return new CreateChatRoomDto(1L, null, "chatRoomName-1234", false, "secretKey-1234", 3);
    }

    public static CreateChatRoomDto createOneToOneChatRoomDto() {
        return new CreateChatRoomDto(1L, 2L, "chatRoomName-1234", false, "secretKey-1234", 2);
    }

    public static ChatRoom creatChatRoom(CreateChatRoomDto createChatRoomDto, ChatRoomStatus chatRoomStatus) {
        return ChatRoom.create(createChatRoomDto, chatRoomStatus);
    }
}
