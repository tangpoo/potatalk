package com.potatalk.controller;

import com.potatalk.dto.ChatMessageDto;
import com.potatalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto messageDto) {
        log.info(">>> Received: {}", messageDto);
        chatService.sendChatMessage(messageDto);
    }

    @PostMapping("/api/v1/chat/message")
    public void messageApiTest(@RequestBody ChatMessageDto messageDto) {
        chatService.sendChatMessage(messageDto);
    }
}
