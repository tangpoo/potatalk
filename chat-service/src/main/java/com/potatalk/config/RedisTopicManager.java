package com.potatalk.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTopicManager {

    public static final String chatRoomTopic = "chatroom:";
    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final MessageListenerAdapter messageListenerAdapter;

    // 해당 채널을 구독하는 메서드
    public void subscribeToTopic(String chatRoomId) {
        ChannelTopic topic = new ChannelTopic(chatRoomTopic + chatRoomId);

        listenerContainer
                .receive(topic)
                .map(message -> (String) message.getMessage())
                .doOnSubscribe(s -> log.info("Subscribed to topic: " + topic.getTopic()))
                .doOnNext(
                        message -> {
                            log.info("Received message from Redis: " + message);

                            // 메시지를 ChatSubscriber에 전달
                            messageListenerAdapter.onMessage(
                                    new DefaultMessage(
                                            topic.getTopic().getBytes(), message.getBytes()),
                                    null);
                        })
                .doOnError(e -> log.error("Error while receiving Redis message", e))
                .doOnComplete(
                        () ->
                                log.info(
                                        "Completed receiving message from Redis: "
                                                + topic.getTopic()))
                .subscribe(); // 구독 시작
    }

    public Mono<ChannelTopic> getTopicForChatRoom(String chatRoomId) {
        return Mono.just(new ChannelTopic(chatRoomTopic + chatRoomId));
    }
}
