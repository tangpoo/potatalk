package com.potatalk.service;

import com.potatalk.domain.Chat;
import com.potatalk.dto.ChatMessageDto;
import com.potatalk.pubsub.ChatPublisher;
import com.potatalk.repository.ChatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatPublisher chatPublisher;

    public void sendChatMessage(final ChatMessageDto messageDto) {
        chatRepository
                .save(Chat.create(messageDto))
                .then(Mono.fromRunnable(() -> chatPublisher.publish(messageDto)))
                .subscribe();
    }
}
