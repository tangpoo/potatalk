package com.potatalk.service;

import static org.mockito.Mockito.*;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;
import com.potatalk.chatroomservice.domain.Participation;
import com.potatalk.chatroomservice.domain.ParticipationStatus;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.repository.ChatRoomRepository;
import com.potatalk.chatroomservice.repository.ParticipationRepository;
import com.potatalk.chatroomservice.service.ChatRoomService;
import com.potatalk.steps.ChatRoomSteps;
import com.potatalk.steps.ParticipationSteps;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTests {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private TransactionalOperator transactionalOperator;

    @Test
    void create_group_chat_room() {
        // Arrange
        final CreateChatRoomDto createChatRoomDto = ChatRoomSteps.createGroupChatRoomDto();
        final ChatRoom chatRoom = ChatRoomSteps.creatChatRoom(createChatRoomDto,
            ChatRoomStatus.GROUP);
        final Participation participation = ParticipationSteps.create(chatRoom.getCreateMemberId(),
            chatRoom.getId(), ParticipationStatus.JOINED);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(Mono.just(chatRoom));
        when(participationRepository.save(any(Participation.class))).thenReturn(Mono.just(participation));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        final Mono<ChatRoom> result = chatRoomService.createChatRoom(createChatRoomDto);

        // Assert
        StepVerifier.create(result).expectNext(chatRoom).verifyComplete();
    }

    @Test
    void create_one_to_one_chat_room() {
        // Arrange
        final CreateChatRoomDto createChatRoomDto = ChatRoomSteps.createOneToOneChatRoomDto();
        final ChatRoom chatRoom = ChatRoomSteps.creatChatRoom(createChatRoomDto,
            ChatRoomStatus.ONE_TO_ONE);
        final Participation participation1 = ParticipationSteps.create(
            createChatRoomDto.getMemberId(),
            chatRoom.getId(), ParticipationStatus.JOINED);
        final Participation participation2 = ParticipationSteps.create(
            createChatRoomDto.getFriendId(),
            chatRoom.getId(), ParticipationStatus.JOINED);

        when(chatRoomRepository.findOneToOneChatRoom(createChatRoomDto.getMemberId(),
            createChatRoomDto.getFriendId(), ChatRoomStatus.ONE_TO_ONE)).thenReturn(Mono.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(Mono.just(chatRoom));
        when(participationRepository.saveAll(anyList())).thenReturn(Flux.just(participation1, participation2));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        final Mono<ChatRoom> result = chatRoomService.createOneToOneChatRoom(createChatRoomDto);

        // Assert
        StepVerifier.create(result).expectNext(chatRoom).verifyComplete();
    }
}
