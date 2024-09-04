package com.potatalk.chatroomservice.repository;

import com.potatalk.chatroomservice.domain.Participation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ParticipationRepository extends R2dbcRepository<Participation, Long> {

}
