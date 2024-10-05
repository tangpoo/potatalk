package com.potatalk.memberservice.repository;

import com.potatalk.memberservice.domain.Friend;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FriendRepository extends R2dbcRepository<Friend, Long> {

    @Query(
            "SELECT * FROM friends WHERE (member_id = :memberId OR friend_id = :memberId) AND"
                + " is_accepted = true")
    Flux<Friend> findAllFriendsByMemberId(@Param("memberId") Long memberId);

    Mono<Friend> findByMemberIdAndFriendId(Long memberId, Long friendId);
}
