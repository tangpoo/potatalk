package com.potatalk.subscriber;

import com.potatalk.config.RedisTopicManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopicMessageSubscriber {

    private final RedisTopicManager topicManager;
    private static final String topicExchange = "messageQueue.exchange.topic";

    @RabbitListener(
            ackMode = "MANUAL",
            id = "addTopicMessageListener",
            bindings =
                    @QueueBinding(
                            value = @Queue,
                            exchange = @Exchange(topicExchange),
                            key = "addTopic"))
    public Mono<Void> processAddTopicMessage(String topic) {
        log.info("Consuming addTopic    ===>    " + topic);
        return topicManager.addTopicForChatRoom(topic)
            .doOnNext(result -> System.out.println("Reactive process onNext: " + result))
            .doOnSuccess(System.out::println)
            .doOnError(e -> System.out.println(e.getMessage()));

    }
}
