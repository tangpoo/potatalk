package com.potatalk.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTopicManager {

    private final RedisMessageListenerContainer listenerContainer;
    private final MessageListenerAdapter messageListenerAdapter;
    private final Map<String, ChannelTopic> topics = new ConcurrentHashMap<>();

    public static final String chatRoomTopic = "chatroom:";

    public void addTopicForChatRoom(String chatRoomId) {
        if (!topics.containsKey(chatRoomId)) {
            ChannelTopic newTopic = new ChannelTopic(chatRoomTopic + chatRoomId);
            topics.put(chatRoomId, newTopic);
            listenerContainer.addMessageListener(messageListenerAdapter, newTopic);
        }
    }

    public void removeTopicForChatRoom(String chatRoomId) {
        ChannelTopic topicToRemove = topics.remove(chatRoomId);
        if (topicToRemove != null) {
            listenerContainer.removeMessageListener(messageListenerAdapter, topicToRemove);
        }
    }

    public ChannelTopic getTopicForChatRoom(String chatRoomId) {
        return topics.get(chatRoomId);
    }
}
