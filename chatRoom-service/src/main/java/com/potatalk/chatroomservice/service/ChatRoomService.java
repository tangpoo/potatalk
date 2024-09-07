package com.potatalk.chatroomservice.service;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;
import com.potatalk.chatroomservice.domain.Participation;
import com.potatalk.chatroomservice.domain.ParticipationStatus;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.exception.ChatRoomNotFound;
import com.potatalk.chatroomservice.exception.PrivateKeyIsNotMatchedException;
import com.potatalk.chatroomservice.repository.ChatRoomRepository;
import com.potatalk.chatroomservice.repository.ParticipationRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipationRepository participationRepository;
    private final TransactionalOperator transactionalOperator;

    public Mono<ChatRoom> createChatRoom(CreateChatRoomDto createChatRoomDto) {
        Long memberId = createChatRoomDto.getMemberId();
        return chatRoomRepository.save(ChatRoom.create(createChatRoomDto, ChatRoomStatus.GROUP))
            .flatMap(chatRoom ->
                participationRepository.save(
                    Participation.create(chatRoom.getId(), memberId, ParticipationStatus.JOINED)
                ).thenReturn(chatRoom)
            )
            .as(transactionalOperator::transactional);
    }

    public Mono<ChatRoom> createOneToOneChatRoom(CreateChatRoomDto createChatRoomDto) {
        Long memberId = createChatRoomDto.getMemberId();
        Long friendId = createChatRoomDto.getFriendId();

        return chatRoomRepository.findOneToOneChatRoom(memberId, friendId, ChatRoomStatus.ONE_TO_ONE)
            .switchIfEmpty(Mono.defer(() -> {
                ChatRoom chatRoom = ChatRoom.create(createChatRoomDto, ChatRoomStatus.ONE_TO_ONE);
                return chatRoomRepository.save(chatRoom);
            }))
            .flatMap(savedRoom -> {
                Participation memberParticipation = Participation.create(memberId, savedRoom.getId(), ParticipationStatus.JOINED);
                Participation friendParticipation = Participation.create(friendId, savedRoom.getId(), ParticipationStatus.JOINED);

                return participationRepository.saveAll(Arrays.asList(memberParticipation, friendParticipation))
                    .then(Mono.just(savedRoom));
            })
            .as(transactionalOperator::transactional);
    }

    public Mono<ChatRoom> joinChatRoom(Long roomId, Long memberId, String secretKey) {
        return chatRoomRepository.findById(roomId)
            .switchIfEmpty(Mono.error(new ChatRoomNotFound("ChatRoom not found")))
            .flatMap(chatRoom -> {
                if (chatRoom.getIsPrivate()) {
                    matchPrivateKey(chatRoom, secretKey);
                }

                chatRoom.joinParticipation();

                return chatRoomRepository.save(chatRoom)
                    .then(participationRepository.save(
                        Participation.create(memberId, chatRoom.getId(), ParticipationStatus.JOINED))
                    )
                    .thenReturn(chatRoom);
            });
    }

    private void matchPrivateKey(ChatRoom chatRoom, String secretKey) {
        if (!chatRoom.matchSecretKey(secretKey)) {
            throw new PrivateKeyIsNotMatchedException("privateKey not matched");
        }
    }
}

