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
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    // 채팅방에 대한 채널을 Redis에 먼저 등록하고 구독을 시작하는 메서드
    public Mono<Void> addTopicForChatRoom(String chatRoomId) {
        log.info("Adding topic for chat room: " + chatRoomId);

        // Redis에 채널을 등록
        ChannelTopic newTopic = new ChannelTopic(chatRoomTopic + chatRoomId);

        return redisTemplate
                .convertAndSend(newTopic.getTopic(), "init") // 메시지를 보내서 Redis에 채널을 등록
                .doOnSuccess(
                        result -> log.info("Redis channel registered for: " + newTopic.getTopic()))
                .doOnError(e -> log.error("Error sending message to Redis: " + e.getMessage()))
                .then(Mono.fromRunnable(() -> subscribeToTopic(newTopic))); // 구독을 시작
    }

    // 해당 채널을 구독하는 메서드
    private void subscribeToTopic(ChannelTopic topic) {
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
