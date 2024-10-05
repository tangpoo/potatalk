package com.potatalk.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTopicManager {

    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final MessageListenerAdapter messageListenerAdapter;
    private final Map<String, ChannelTopic> topics = new ConcurrentHashMap<>();
    private final Map<String, Disposable> subscriptions = new ConcurrentHashMap<>();

    public static final String chatRoomTopic = "chatroom:";

    public Mono<Void> addTopicForChatRoom(String chatRoomId) {
        log.info("in topicManager");
        return Mono.fromRunnable(
                        () -> {
                            if (!topics.containsKey(chatRoomId)) {
                                ChannelTopic newTopic =
                                        new ChannelTopic(chatRoomTopic + chatRoomId);
                                topics.put(chatRoomId, newTopic);

                                Flux<String> messageFlux =
                                        listenerContainer
                                                .receive(newTopic)
                                                .map(message -> (String) message.getMessage())
                                                .doOnSubscribe(
                                                        s ->
                                                                log.info(
                                                                        "Subscribed to topic: "
                                                                                + newTopic
                                                                                        .getTopic()))
                                                .doOnNext(
                                                        message ->
                                                                log.info(
                                                                        "Received message from"
                                                                                + " Redis: "
                                                                                + message))
                                                .doOnError(
                                                        e ->
                                                                log.error(
                                                                        "Error while receiving"
                                                                                + " Redis message",
                                                                        e));

                                Disposable subscription =
                                        messageFlux.subscribe(
                                                message -> {
                                                    log.info(
                                                            "Received message from Redis: "
                                                                    + message);

                                                    // Redis 채널 이름을 가져와 DefaultMessage에 전달
                                                    ChannelTopic topic = topics.get(chatRoomId);
                                                    if (topic != null) {
                                                        byte[] channel =
                                                                topic.getTopic()
                                                                        .getBytes(); // 채널을 topic에서
                                                        // 가져옴
                                                        Message redisMessage =
                                                                new DefaultMessage(
                                                                        channel,
                                                                        message.getBytes());
                                                        messageListenerAdapter.onMessage(
                                                                redisMessage, null);
                                                    } else {
                                                        log.error(
                                                                "Topic not found for chatRoomId: "
                                                                        + chatRoomId);
                                                    }
                                                });

                                subscriptions.put(chatRoomId, subscription);
                                log.info("topic.get: " + topics.get(chatRoomId));
                            }
                        })
                .then();
    }

    public Mono<Void> removeTopicForChatRoom(String chatRoomId) {
        return Mono.fromRunnable(
                        () -> {
                            ChannelTopic topicToRemove = topics.remove(chatRoomId);
                            if (topicToRemove != null) {
                                Disposable subscription = subscriptions.remove(chatRoomId);
                                if (subscription != null && !subscription.isDisposed()) {
                                    subscription.dispose();
                                }
                            }
                        })
                .then();
    }

    public Mono<ChannelTopic> getTopicForChatRoom(String chatRoomId) {
        return Mono.justOrEmpty(topics.get(chatRoomId));
    }
}
