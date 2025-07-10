package com.potatalk.config;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DefaultMessage;
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
    private final Set<String> subscribedTopicSet = ConcurrentHashMap.newKeySet();

    // 해당 채널을 구독하는 메서드
    public void subscribeToTopic(String chatRoomId) {
        log.info("hello? here is sub");
        ChannelTopic topic = new ChannelTopic(chatRoomTopic + chatRoomId);
        log.info("Try Subscribe");

        String topicName = chatRoomTopic + chatRoomId;

        if (!subscribedTopicSet.add(topicName)) {
            log.info("이미 Redis에 구독된 Topic입니다: {}", topicName);
            return;
        }

        listenerContainer
            .receive(topic)
            .map(message -> message.getMessage())
            .doOnSubscribe(s -> log.info("Subscribed to topic: " + topic.getTopic()))
            .doOnNext(
                message -> {
                    log.info("Received message from Redis: " + message);

                    // 메시지를 ChatSubscriber에 전달
                    messageListenerAdapter.onMessage(
                        new DefaultMessage(
                            topic.getTopic().getBytes(), message.getBytes(
                            StandardCharsets.UTF_8)),
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
