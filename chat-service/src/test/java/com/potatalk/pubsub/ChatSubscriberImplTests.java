package com.potatalk.pubsub;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potatalk.dto.ChatMessageDto;
import com.potatalk.exception.MessageSendException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@ExtendWith(MockitoExtension.class)
public class ChatSubscriberImplTests {

    @InjectMocks private ChatSubscriberImpl chatSubscriber;

    @Mock private ObjectMapper objectMapper;

    @Mock private SimpMessageSendingOperations messageTemplate;

    @Test
    void send_message_should_send_message_to_correct_destination() throws Exception {
        // Arrange
        String message =
                "{\"id\":\"id-1234\", \"roomId\":\"roomId-1234\", \"sender\":\"sender-1234\","
                    + " \"message\":\"Hello!\"}";
        ChatMessageDto messageDto =
                new ChatMessageDto("id-1234", "roomId-1234", "sender-1234", message);

        when(objectMapper.readValue(message, ChatMessageDto.class)).thenReturn(messageDto);

        // Act
        chatSubscriber.sendMessage(message);

        // Assert
        verify(messageTemplate, times(1))
                .convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
    }

    @Test
    void send_message_should_throw_exception_on_invalid_message_format() throws Exception {
        // Arrange
        String invalidMessage = "Invalid JSON format";

        when(objectMapper.readValue(invalidMessage, ChatMessageDto.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        // Act + Assert
        Assertions.assertThrows(
                MessageSendException.class, () -> chatSubscriber.sendMessage(invalidMessage));
    }
}
