package com.potatalk.subscriber;

import com.potatalk.config.RedisTopicManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageSubscriber {

    private final RedisTopicManager topicManager;
    private static final String topic = "kafka.topic";

    @KafkaListener(topics = topic, groupId = "potatalk-group")
    public void listen(String message) {
        log.info("Consuming addTopic    ===>    " + message);
        topicManager.subscribeToTopic(message);
    }
}
