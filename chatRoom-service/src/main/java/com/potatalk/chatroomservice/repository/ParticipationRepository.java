package com.potatalk.chatroomservice.repository;

import com.potatalk.chatroomservice.domain.Participation;
import com.potatalk.chatroomservice.domain.ParticipationStatus;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ParticipationRepository extends R2dbcRepository<Participation, Long> {

    @Query(
            "SELECT * FROM participation p "
                    + "WHERE p.participation_status = :participationStatus "
                    + "AND p.member_id = :memberId")
    Flux<Participation> findAllByParticipationStatusIsInvited(
            @Param("memberId") Long memberId,
            @Param("participationStatus") ParticipationStatus participationStatus);

    Mono<Participation> findByRoomIdAndMemberId(Long roomId, Long memberId);

    @Query("SELECT p.id " + "FROM participation p ON p.roomId = :roomId")
    Flux<Long> findAllIdByRoomId(@Param("roomId") Long roomId);
}
