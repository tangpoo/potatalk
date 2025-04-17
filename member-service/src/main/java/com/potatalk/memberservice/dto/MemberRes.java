package com.potatalk.memberservice.dto;

import com.potatalk.memberservice.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public class MemberRes {

    private String username;

    private String nickName;

    public static MemberRes from(Member member) {
        return new MemberRes(member.getUsername(), member.getNickName());
    }
}
