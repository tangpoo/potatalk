package com.potatalk.chatroomservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomPublisherImpl implements ChatRoomPublisher {

    private final AmqpTemplate messageQueue;
    private final String topicExchange = "messageQueue.exchange.topic";

    @Override
    public Mono<String> sendAddTopicEvent(final String roomId) {
        return Mono.just(roomId)
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::publishAddTopicEvent);
    }

    private Mono<String> publishAddTopicEvent(String topic) {
        return Mono.fromCallable(
            () -> {
                messageQueue.convertAndSend(topicExchange, "addTopic", topic);
                return topic;
            }
        );
    }
}
