package com.potatalk.pubsub;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ChatEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendJoinEvent(String roomId, String username) {
        String message = String.format("User %s has joined room %s", username, roomId);
        kafkaTemplate.send("chat.join", roomId, message); // topic, key, value
    }
}
