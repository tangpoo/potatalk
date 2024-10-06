package com.potatalk.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.potatalk.config.RedisTopicManager;
import com.potatalk.dto.ChatMessageDto;
import com.potatalk.subscriber.TopicMessageSubscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChatWebSocketTests {

    @Container
    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongodb/mongodb-community-server:latest");

    @Autowired private RedisTopicManager topicManager;

    @Autowired private TopicMessageSubscriber subscriber;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @LocalServerPort private int randomPort;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setup() {
        wsUrl = String.format("ws://localhost:%d/ws/chat", randomPort); // WebSocket 엔드포인트

        List<Transport> transports =
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = messageConverter.getObjectMapper();
        objectMapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());
        stompClient.setMessageConverter(messageConverter);
    }

    @Test
    public void send_message_to_chat_room() throws Exception {
        // WebSocket 연결 설정
        StompSession session =
                stompClient
                        .connect(
                                wsUrl,
                                new WebSocketHttpHeaders(),
                                new StompSessionHandlerAdapter() {})
                        .get(5, TimeUnit.SECONDS);

        // 테스트용 메시지 생성
        ChatMessageDto messageDto = new ChatMessageDto("roomId-1234", "sender-1234", "message");

        // 토픽 등록
        subscriber.processAddTopicMessage(messageDto.getRoomId()).block();

        // 메세지를 받을 큐 설정
        BlockingQueue<ChatMessageDto> blockingQueue = new LinkedBlockingQueue<>();

        // 구독 설정
        Subscription subscribe =
                session.subscribe(
                        "/sub/chat/room/" + messageDto.getRoomId(),
                        new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return ChatMessageDto.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                blockingQueue.add((ChatMessageDto) payload);
                            }
                        });

        // WebSocket을 통해 메시지 발행
        session.send("/pub/chat/message", messageDto);

        // 큐에서 메시지 가져오기 (최대 5초 대기)
        ChatMessageDto receivedMessage = blockingQueue.poll(3, TimeUnit.SECONDS);

        // 검증
        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.getRoomId()).isEqualTo("roomId-1234");
        assertThat(receivedMessage.getMessage()).isEqualTo("message");

        // 구독 취소
        subscribe.unsubscribe();
    }
}
