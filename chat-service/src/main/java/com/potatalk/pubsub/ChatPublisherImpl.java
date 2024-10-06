package com.potatalk.pubsub;

import com.potatalk.config.RedisTopicManager;
import com.potatalk.dto.ChatMessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPublisherImpl implements ChatPublisher {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final RedisTopicManager topicManager;

    @Override
    public void publish(final ChatMessageDto message) {
        ChannelTopic topic = topicManager.getTopicForChatRoom(message.getRoomId()).block();
        log.info(topic.getTopic());
        if (topic != null) {
            redisTemplate.convertAndSend(topic.getTopic(), message).subscribe();
        } else {
            log.warn("Topic for chat room {} dies not exist!", message.getRoomId());
        }
    }
}
