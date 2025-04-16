package com.potatalk.pubsub;

import com.potatalk.metric.WebSocketMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebSocketMetrics webSocketMetrics;

    public ChatEventProducer(KafkaTemplate<String, String> kafkaTemplate, WebSocketMetrics webSocketMetrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.webSocketMetrics = webSocketMetrics;
    }

    public void sendJoinEvent(String roomId, String username) {
        String message = String.format("User %s has joined room %s", username, roomId);
        log.info(message);
        webSocketMetrics.recordMessage(() ->
            kafkaTemplate.send("chat.join", roomId, message).join()
        );
    }
}
