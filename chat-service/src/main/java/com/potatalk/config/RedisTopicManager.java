package com.potatalk.config;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final ReactiveStringRedisTemplate redisTemplate;

    // 해당 채널을 구독하는 메서드
    public void subscribeToTopic(String chatRoomId) {
        String topicKey = "chatroom:subscription:" + chatRoomId;
        ChannelTopic topic = new ChannelTopic(chatRoomTopic + chatRoomId);
        log.info("Try Subscribe");

        redisTemplate.opsForValue().setIfAbsent(topicKey, "subscribed")
            .flatMap(wasSet -> {
                if (!wasSet) {
                    log.info("이미 구독된 Redis 채널입니다: {}", topic.getTopic());
                    return Mono.empty();
                }

                return listenerContainer.receive(topic)
                    .map(message -> (String) message.getMessage())
                    .doOnSubscribe(s -> log.info("✅ Redis 구독 시작됨: " + topic.getTopic()))
                    .doOnNext(message -> {
                        log.info("✅ Redis 메시지 수신: " + message);
                        messageListenerAdapter.onMessage(
                            new DefaultMessage(topic.getTopic().getBytes(), message.getBytes(StandardCharsets.UTF_8)),
                            null
                        );
                    })
                    .doOnError(e -> log.error("Redis Pub/Sub Error", e))
                    .then();
            })
            .subscribe();
    }

    public Mono<ChannelTopic> getTopicForChatRoom(String chatRoomId) {
        return Mono.just(new ChannelTopic(chatRoomTopic + chatRoomId));
    }
}
