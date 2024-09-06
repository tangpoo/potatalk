package com.potatalk.steps;

import com.potatalk.chatroomservice.domain.Participation;
import com.potatalk.chatroomservice.domain.ParticipationStatus;

public class ParticipationSteps {

    public static Participation create(Long memberId, Long chatRoomId, ParticipationStatus participationStatus) {
        return Participation.create(memberId, chatRoomId, participationStatus);
    }
}
