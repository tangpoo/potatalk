package com.potatalk.controller;

import com.potatalk.dto.ChatMessageDto;
import com.potatalk.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageDto messageDto) {
        chatService.sendChatMessage(messageDto);
    }
}
