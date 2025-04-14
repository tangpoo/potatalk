package com.potatalk.chatroomservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaPublisherImpl {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "kafka.topic";

    public Mono<String> sendAddTopicEvent(String message) {
        return Mono.fromRunnable(() -> kafkaTemplate.send(TOPIC, message))
            .then(Mono.just(message));
    }
}
