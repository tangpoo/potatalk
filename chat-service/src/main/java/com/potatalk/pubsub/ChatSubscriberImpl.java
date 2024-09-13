package com.potatalk.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potatalk.dto.ChatMessageDto;
import com.potatalk.exception.MessageSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatSubscriberImpl implements ChatSubscriber {

    private final ObjectMapper om;
    private final SimpMessageSendingOperations messageTemplate;

    @Override
    public void sendMessage(final String message) {
        try {
            ChatMessageDto chatMessageDto = om.readValue(message, ChatMessageDto.class);
            messageTemplate.convertAndSend(
                "/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MessageSendException();
        }
    }
}
