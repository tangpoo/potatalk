package com.potatalk.config;

import com.potatalk.pubsub.ChatEventProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final ChatEventProducer chatEventProducer;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("join preSend");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 클라이언트가 WebSocket을 통해 연결할 때 처리 로직
            log.info("CONNECT 확인");
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // 클라이언트가 채팅방에 입장할 때 처리 로직
            String destination = accessor.getDestination();
            String roomId = extractRoomId(destination);
            chatEventProducer.sendJoinEvent(roomId, "test-user");

        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            // 클라이언트가 메시지를 보낼 때 처리 로직
        } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            // 클라이언트가 채팅방에 퇴장할 때 처리 로직
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private String extractRoomId(String destination) {
        if (destination != null && destination.startsWith("/sub/chat/room/")) {
            return destination.replace("/sub/chat/room/", "");
        }
        return "unknown-room";
    }
}
