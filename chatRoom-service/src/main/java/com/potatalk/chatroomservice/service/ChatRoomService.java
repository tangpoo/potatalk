package com.potatalk.chatroomservice.service;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.Participation;
import com.potatalk.chatroomservice.dto.CreateChatRoomDto;
import com.potatalk.chatroomservice.repository.ChatRoomRepository;
import com.potatalk.chatroomservice.repository.ParticipationRepository;
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

    public Mono<ChatRoom> createChatRoom(CreateChatRoomDto  createChatRoomDto) {
        return chatRoomRepository.save(ChatRoom.create(createChatRoomDto))
            .flatMap(chatRoom ->
                participationRepository.save(
                    Participation.create(chatRoom.getId(), createChatRoomDto.getMemberId()
                    )
                ).thenReturn(chatRoom)
            )
            .as(transactionalOperator::transactional);
    }
}

