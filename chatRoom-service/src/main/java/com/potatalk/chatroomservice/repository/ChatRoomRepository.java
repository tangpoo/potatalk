package com.potatalk.chatroomservice.repository;

import com.potatalk.chatroomservice.domain.ChatRoom;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ChatRoomRepository extends R2dbcRepository<ChatRoom, Long> {

}
