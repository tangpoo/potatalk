package com.potatalk.chatroomservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("participation")
@AllArgsConstructor
@Getter
public class Participation {

    @Id
    private Long id;

    private Long memberId;

    private Long chatRoomId;

    private boolean isJoined = false;

    private Participation(final Long memberId, final Long chatRoomId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
    }

    public static Participation create(Long memberId, Long chatROomId) {
        return new Participation(memberId, chatROomId);
    }

    public void join() {
        this.isJoined = true;
    }
}
