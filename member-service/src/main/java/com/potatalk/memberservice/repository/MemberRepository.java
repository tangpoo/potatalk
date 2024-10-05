package com.potatalk.memberservice.repository;

import com.potatalk.memberservice.domain.Member;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends R2dbcRepository<Member, Long> {

    Mono<Member> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}
