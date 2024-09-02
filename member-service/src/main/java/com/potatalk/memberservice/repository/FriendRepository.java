package com.potatalk.memberservice.repository;

import com.potatalk.memberservice.domain.Friend;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FriendRepository extends R2dbcRepository<Friend, Long> {

}
