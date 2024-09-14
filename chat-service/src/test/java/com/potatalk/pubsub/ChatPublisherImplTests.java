package com.potatalk.pubsub;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.potatalk.config.RedisTopicManager;
import com.potatalk.dto.ChatMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
public class ChatPublisherImplTests {

    @InjectMocks
    private ChatPublisherImpl chatPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisTopicManager topicManager;

    @Test
    void publish_should_send_message_to_correct_topic() {
        // Arrange
        ChatMessageDto message = new ChatMessageDto("id-1234", "roomId-1234", "sender-1234",
            "message");
        ChannelTopic channelTopic = new ChannelTopic("chatroom:roomId-1234");

        when(topicManager.getTopicForChatRoom("roomId-1234")).thenReturn(channelTopic);

        // Act
        chatPublisher.publish(message);

        // Assert
        verify(redisTemplate, times(1)).convertAndSend(channelTopic.getTopic(), message);
    }

    @Test
    void publish_should_not_send_message_if_topic_does_not_exist() {
        // Arrange
        ChatMessageDto message = new ChatMessageDto("id-1234", "roomId-1234", "sender-1234",
            "message");

        when(topicManager.getTopicForChatRoom("roomId-1234")).thenReturn(null);

        // Act
        chatPublisher.publish(message);

        // Assert
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}
