package com.potatalk.memberservice.repository;

import com.potatalk.memberservice.domain.Member;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends R2dbcRepository<Member, Long> {

}
