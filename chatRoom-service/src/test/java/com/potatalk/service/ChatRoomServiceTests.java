package com.potatalk.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;
import com.potatalk.chatroomservice.domain.Participation;
import com.potatalk.chatroomservice.domain.ParticipationStatus;
import com.potatalk.chatroomservice.dto.ChatRoomInfoRes;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.exception.PrivateKeyIsNotMatchedException;
import com.potatalk.chatroomservice.publisher.ChatRoomPublisher;
import com.potatalk.chatroomservice.repository.ChatRoomRepository;
import com.potatalk.chatroomservice.repository.ParticipationRepository;
import com.potatalk.chatroomservice.service.ChatRoomService;
import com.potatalk.steps.ChatRoomSteps;
import com.potatalk.steps.ParticipationSteps;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTests {

    @InjectMocks private ChatRoomService chatRoomService;

    @Mock private ChatRoomRepository chatRoomRepository;

    @Mock private ParticipationRepository participationRepository;

    @Mock private ChatRoomPublisher chatRoomPublisher;

    @Mock private TransactionalOperator transactionalOperator;

    @Test
    void create_group_chat_room() {
        // Arrange
        final CreateChatRoomDto createChatRoomDto =
                ChatRoomSteps.createGroupChatRoomDto(false, null);
        final ChatRoom chatRoom =
                ChatRoomSteps.createChatRoom(createChatRoomDto, ChatRoomStatus.GROUP);
        final ChatRoom spyChatRoom = spy(chatRoom);
        doReturn(1L).when(spyChatRoom).getId();
        final Participation participation =
                ParticipationSteps.create(
                        chatRoom.getCreateMemberId(), chatRoom.getId(), ParticipationStatus.JOINED);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(Mono.just(spyChatRoom));
        when(participationRepository.save(any(Participation.class)))
                .thenReturn(Mono.just(participation));
        when(chatRoomPublisher.sendAddTopicEvent(any())).thenReturn(Mono.just("roomId-1"));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        final Mono<ChatRoom> result = chatRoomService.createChatRoom(createChatRoomDto);

        // Assert
        StepVerifier.create(result).expectNext(spyChatRoom).verifyComplete();
    }

    @Test
    void create_one_to_one_chat_room() {
        // Arrange
        final CreateChatRoomDto createChatRoomDto = ChatRoomSteps.createOneToOneChatRoomDto();
        final ChatRoom chatRoom =
                ChatRoomSteps.createChatRoom(createChatRoomDto, ChatRoomStatus.ONE_TO_ONE);
        final ChatRoom spyChatRoom = spy(chatRoom);
        doReturn(1L).when(spyChatRoom).getId();
        final Participation participation1 =
                ParticipationSteps.create(
                        createChatRoomDto.getMemberId(),
                        spyChatRoom.getId(),
                        ParticipationStatus.JOINED);
        final Participation participation2 =
                ParticipationSteps.create(
                        createChatRoomDto.getFriendId(),
                        spyChatRoom.getId(),
                        ParticipationStatus.JOINED);

        when(chatRoomRepository.findOneToOneChatRoom(
                        createChatRoomDto.getMemberId(),
                        createChatRoomDto.getFriendId(),
                        ChatRoomStatus.ONE_TO_ONE))
                .thenReturn(Mono.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(Mono.just(spyChatRoom));
        when(participationRepository.saveAll(anyList()))
                .thenReturn(Flux.just(participation1, participation2));
        when(chatRoomPublisher.sendAddTopicEvent(any())).thenReturn(Mono.just("roomId-1"));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        final Mono<ChatRoom> result = chatRoomService.createOneToOneChatRoom(createChatRoomDto);

        // Assert
        StepVerifier.create(result).expectNext(spyChatRoom).verifyComplete();
    }

    @Nested
    class Join_chat_room {

        @Test
        void success_private_room() {
            // Arrange
            Long roomId = 1L;
            Long memberId = 1L;
            String secretKey = "secretKey-1234";

            final ChatRoom chatRoom = ChatRoomSteps.createChatRoom();
            final ChatRoom spyChatRoom = spy(chatRoom);
            doReturn(1L).when(spyChatRoom).getId();
            final Participation participation =
                    ParticipationSteps.create(memberId, roomId, ParticipationStatus.JOINED);

            when(chatRoomRepository.findById(anyLong())).thenReturn(Mono.just(spyChatRoom));
            when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(Mono.just(spyChatRoom));
            when(participationRepository.save(any(Participation.class)))
                    .thenReturn(Mono.just(participation));

            // Act
            final Mono<ChatRoom> result = chatRoomService.joinChatRoom(roomId, memberId, secretKey);

            // Assert
            StepVerifier.create(result).expectNext(spyChatRoom).verifyComplete();
            verify(chatRoomRepository, times(1)).findById(roomId);
            verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
            verify(participationRepository, times(1)).save(any(Participation.class));
        }

        @Test
        void diff_private_key() {
            // Arrange
            Long roomId = 1L;
            Long memberId = 1L;
            String diffSecretKey = "diffKey-1234";

            final ChatRoom chatRoom = ChatRoomSteps.createPrivateChatRoom();

            when(chatRoomRepository.findById(roomId)).thenReturn(Mono.just(chatRoom));

            // Act
            final Mono<ChatRoom> result =
                    chatRoomService.joinChatRoom(roomId, memberId, diffSecretKey);

            // Assert
            StepVerifier.create(result).expectError(PrivateKeyIsNotMatchedException.class).verify();
            verify(chatRoomRepository, times(1)).findById(roomId);
            verify(chatRoomRepository, times(0)).save(any(ChatRoom.class));
            verify(participationRepository, times(0)).save(any(Participation.class));
        }
    }

    @Nested
    class Invited_chatRoom {

        @Test
        void success() {
            // Arrange
            Long memberId = 1L;
            Long roomId = 1L;
            ChatRoom chatRoom = ChatRoomSteps.createChatRoom();
            ChatRoom spyChatRoom = spy(chatRoom);
            doReturn(1L).when(spyChatRoom).getId();
            Participation participation =
                    ParticipationSteps.create(memberId, roomId, ParticipationStatus.INVITED);

            when(chatRoomRepository.findById(roomId)).thenReturn(Mono.just(spyChatRoom));
            when(participationRepository.findByRoomIdAndMemberId(roomId, memberId))
                    .thenReturn(Mono.empty());
            when(participationRepository.save(any(Participation.class)))
                    .thenReturn(Mono.just(participation));

            // Act
            final Mono<ChatRoom> result = chatRoomService.inviteChatRoom(roomId, memberId);

            // Assert
            StepVerifier.create(result).expectNext(spyChatRoom).verifyComplete();
            verify(chatRoomRepository, times(1)).findById(roomId);
            verify(participationRepository, times(1)).findByRoomIdAndMemberId(roomId, memberId);
            verify(participationRepository, times(1)).save(any(Participation.class));
        }

        @Test
        void fail_exist_and_invited_member() {
            // Arrange
            Long memberId = 1L;
            Long roomId = 1L;
            ChatRoom chatRoom = ChatRoomSteps.createChatRoom();
            ChatRoom spyChatRoom = spy(chatRoom);
            doReturn(1L).when(spyChatRoom).getId();
            Participation participation =
                    ParticipationSteps.create(memberId, roomId, ParticipationStatus.INVITED);

            when(chatRoomRepository.findById(roomId)).thenReturn(Mono.just(spyChatRoom));
            when(participationRepository.findByRoomIdAndMemberId(roomId, memberId))
                    .thenReturn(Mono.just(participation));

            // Act
            final Mono<ChatRoom> result = chatRoomService.inviteChatRoom(roomId, memberId);

            // Assert
            StepVerifier.create(result).expectError(IllegalArgumentException.class).verify();
            verify(chatRoomRepository, times(1)).findById(roomId);
            verify(participationRepository, times(1)).findByRoomIdAndMemberId(roomId, memberId);
            verify(participationRepository, times(0)).save(any(Participation.class));
        }

        @Test
        void fail_exist_and_left_member() {
            // Arrange
            Long memberId = 1L;
            Long roomId = 1L;
            ChatRoom chatRoom = ChatRoomSteps.createChatRoom();
            ChatRoom spyChatRoom = spy(chatRoom);
            doReturn(1L).when(spyChatRoom).getId();
            Participation participation =
                    ParticipationSteps.create(memberId, roomId, ParticipationStatus.LEFT);

            when(chatRoomRepository.findById(roomId)).thenReturn(Mono.just(spyChatRoom));
            when(participationRepository.findByRoomIdAndMemberId(roomId, memberId))
                    .thenReturn(Mono.just(participation));
            when(participationRepository.save(any(Participation.class)))
                    .thenReturn(Mono.just(participation));

            // Act
            final Mono<ChatRoom> result = chatRoomService.inviteChatRoom(roomId, memberId);

            // Assert
            StepVerifier.create(result).expectNext(spyChatRoom).verifyComplete();
            verify(chatRoomRepository, times(1)).findById(roomId);
            verify(participationRepository, times(1)).findByRoomIdAndMemberId(roomId, memberId);
            verify(participationRepository, times(1)).save(any(Participation.class));
        }

        @Test
        void fail_full_participation_chat_room() {
            // Arrange
            Long memberId = 1L;
            Long roomId = 1L;
            ChatRoom chatRoom = ChatRoomSteps.createFullParticipationChatRoom();

            when(chatRoomRepository.findById(roomId)).thenReturn(Mono.just(chatRoom));

            // Act
            final Mono<ChatRoom> result = chatRoomService.inviteChatRoom(roomId, memberId);

            // Assert
            StepVerifier.create(result).expectError(IllegalArgumentException.class).verify();
            verify(chatRoomRepository, times(1)).findById(roomId);
            verify(participationRepository, times(0)).findByRoomIdAndMemberId(roomId, memberId);
            verify(participationRepository, times(0)).save(any(Participation.class));
        }
    }

    @Test
    void find_chat_room_info() {
        // Arrange
        Long roomId = 1L;
        ChatRoom chatRoom = ChatRoomSteps.createChatRoom();
        List<Long> participationIds = List.of(1L, 2L, 3L);

        when(chatRoomRepository.findById(roomId)).thenReturn(Mono.just(chatRoom));
        when(participationRepository.findAllIdByRoomId(roomId))
                .thenReturn(Flux.fromIterable(participationIds));

        // Act
        Mono<ChatRoomInfoRes> result = chatRoomService.findChatRoomInfo(roomId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(
                        chatRoomInfoRes ->
                                chatRoomInfoRes.getParticipationIds().equals(participationIds)
                                        && chatRoomInfoRes
                                                .getCreateMemberId()
                                                .equals(chatRoom.getCreateMemberId())
                                        && chatRoomInfoRes
                                                .getRoomName()
                                                .equals(chatRoom.getRoomName())
                                        && chatRoomInfoRes
                                                .getIsPrivate()
                                                .equals(chatRoom.getIsPrivate())
                                        && chatRoomInfoRes
                                                .getChatRoomStatus()
                                                .equals(chatRoom.getChatRoomStatus())
                                        && chatRoomInfoRes
                                                .getMaxParticipation()
                                                .equals(chatRoom.getMaxParticipation())
                                        && chatRoomInfoRes
                                                .getParticipationCount()
                                                .equals(chatRoom.getParticipationCount()))
                .verifyComplete();
    }
}
