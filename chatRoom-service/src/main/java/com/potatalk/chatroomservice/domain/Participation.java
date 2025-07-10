package com.potatalk.chatroomservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("participation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Participation {

    @Id private Long id;

    private Long memberId;

    private Long roomId;

    private ParticipationStatus participationStatus;

    private Participation(
            final Long memberId, final Long roomId, final ParticipationStatus participationStatus) {
        this.memberId = memberId;
        this.roomId = roomId;
        this.participationStatus = participationStatus;
    }

    public static Participation create(
            Long memberId, Long chatRoomId, ParticipationStatus participationStatus) {
        return new Participation(memberId, chatRoomId, participationStatus);
    }

    public void join() {
        this.participationStatus = ParticipationStatus.JOINED;
    }

    public void invite() {
        this.participationStatus = ParticipationStatus.INVITED;
    }
}
