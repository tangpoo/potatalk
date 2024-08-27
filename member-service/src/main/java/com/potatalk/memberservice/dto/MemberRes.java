package com.potatalk.memberservice.dto;

import com.potatalk.memberservice.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
@Slf4j
public class MemberRes {

    private String username;

    private String nickName;

    public static Mono<MemberRes> from(Member member) {
        return Mono.just(new MemberRes(
            member.getUsername(),
            member.getNickName()
        ));
    }
}
