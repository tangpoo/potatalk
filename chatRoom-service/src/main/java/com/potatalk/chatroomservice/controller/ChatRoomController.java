package com.potatalk.chatroomservice.controller;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatroom")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public Mono<ResponseEntity<ChatRoom>> createChatRoom(
        @RequestBody CreateChatRoomDto createChatRoomDto) {
        return chatRoomService.createChatRoom(createChatRoomDto)
            .map(res -> ResponseEntity.status(HttpStatus.CREATED).body(res));
    }

    @PostMapping
    public Mono<ResponseEntity<ChatRoom>> creatOneToOneChatRoom(
        @RequestBody CreateChatRoomDto createChatRoomDto) {
        return chatRoomService.createOneToOneChatRoom(createChatRoomDto)
            .map(res -> ResponseEntity.status(HttpStatus.CREATED).body(res));
    }
}
