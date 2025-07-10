package com.potatalk.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatJoinConsumer {

    @RabbitListener(queues = "chat.join.queue")
    public void listen(String msg) {
        log.info("[RabbitMQ] Consumed: {}", msg);
    }
}
