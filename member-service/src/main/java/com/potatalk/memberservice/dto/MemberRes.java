package com.potatalk.memberservice.dto;

import com.potatalk.memberservice.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
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
