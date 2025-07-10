package com.codingtest.callservice.controller;


import com.codingtest.callservice.controller.dto.SignalMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SignalController {

    private final SimpMessagingTemplate messageTemplate;
    private final AmqpTemplate messageQueue;

    @MessageMapping("/signal/{roomId}")
    public void signaling(
        @DestinationVariable String roomId,
        @Payload SignalMessage message
    ) {
        messageTemplate.convertAndSend("/topic/signal/" + roomId, message);
    }

    @MessageMapping("/signal/subscribe-room")
    public void handleSubscribeRoom(@Payload SignalMessage message) {
        messageQueue.convertAndSend(
            "messageQueue.exchange.topic",
            "addTopic",
            message.getRoomId().toString()
        );
    }
}
