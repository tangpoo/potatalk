package com.potatalk.chatroomservice.controller;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatroom")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/group")
    public Mono<ResponseEntity<ChatRoom>> createChatRoom(
        @RequestBody CreateChatRoomDto createChatRoomDto) {
        return chatRoomService.createChatRoom(createChatRoomDto)
            .map(res -> ResponseEntity.status(HttpStatus.CREATED).body(res));
    }

    @PostMapping("/one_to_one")
    public Mono<ResponseEntity<ChatRoom>> creatOneToOneChatRoom(
        @RequestBody CreateChatRoomDto createChatRoomDto) {
        return chatRoomService.createOneToOneChatRoom(createChatRoomDto)
            .map(res -> ResponseEntity.status(HttpStatus.CREATED).body(res));
    }

    @PostMapping("/{roomId}")
    public Mono<ResponseEntity<ChatRoom>> joinChatRoom(
        @PathVariable Long roomId,
        @RequestParam Long memberId,
        @RequestParam String secretKey) {
        return chatRoomService.joinChatRoom(roomId, memberId, secretKey)
            .map(res -> ResponseEntity.status(HttpStatus.OK).body(res));
    }

    @PostMapping("/{roomId}/invite/{memberId}")
    public Mono<ResponseEntity<ChatRoom>> inviteChatRoom(
        @PathVariable Long roomId,
        @PathVariable Long memberId
    ) {
        return chatRoomService.inviteChatRoom(roomId, memberId)
            .map(res -> ResponseEntity.status(HttpStatus.OK).body(res));
    }
}
