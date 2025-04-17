package com.potatalk.pubsub;

import com.potatalk.metric.WebSocketMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventProducer {

    private final AmqpTemplate amqpTemplate;
    private final WebSocketMetrics webSocketMetrics;

    public void sendJoinEvent(String roomId, String username) {
        String message = String.format("User %s has joined room %s", username, roomId);
        log.info(message);
        webSocketMetrics.recordMessage(() ->
            amqpTemplate.convertAndSend("chat.join", roomId, message)
        );
    }
}
