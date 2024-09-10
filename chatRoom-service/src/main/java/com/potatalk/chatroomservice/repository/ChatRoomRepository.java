package com.potatalk.chatroomservice.repository;

import com.potatalk.chatroomservice.domain.ChatRoom;
import com.potatalk.chatroomservice.domain.ChatRoomStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends R2dbcRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM chat_rooms cr "
        + "JOIN participation p1 ON cr.id = p1.chatRoomId "
        + "JOIN parcitipation p2 ON cr.id = p2.chatRoomId "
        + "WHERE cr.chatRoomStatus = :chatRoomStatus "
        + "AND p1.memberId = :memberId AND p2.memberId = :friendId")
    Mono<ChatRoom> findOneToOneChatRoom(@Param("memberId") Long memberId, @Param("friendId") Long friendId, ChatRoomStatus chatRoomStatus);

    Flux<ChatRoom> findAllByCreateMemberId(Long memberId);
}
