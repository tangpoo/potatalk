package com.potatalk.chatroomservice.repository;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends R2dbcRepository<ChatRoom, Long> {

    @Query("""
    SELECT cr.*
    FROM chat_rooms cr
    JOIN participation p1 ON cr.id = p1.room_id
    JOIN participation p2 ON cr.id = p2.room_id
    WHERE cr.chat_room_status = :chatRoomStatus
      AND p1.member_id = :memberId
      AND p2.member_id = :friendId
""")
    Mono<ChatRoom> findOneToOneChatRoom(
            @Param("memberId") Long memberId,
            @Param("friendId") Long friendId,
            @Param("chatRoomStatus") ChatRoomStatus chatRoomStatus);

    Flux<ChatRoom> findAllByCreateMemberId(Long memberId);
}
