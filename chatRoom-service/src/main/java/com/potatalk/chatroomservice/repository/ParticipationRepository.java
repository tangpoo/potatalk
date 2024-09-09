package com.potatalk.chatroomservice.repository;

import com.potatalk.chatroomservice.domain.Participation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ParticipationRepository extends R2dbcRepository<Participation, Long> {

    Flux<Participation> findAllByMemberId(Long memberId);

    Mono<Participation> findByRoomIdAndMemberId(Long roomId, Long memberId);
}
