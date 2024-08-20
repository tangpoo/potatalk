package com.potatalk.memberservice.service;

import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberCreateDto;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<MemberRes> createMember(final MemberCreateDto memberCreateDto) {
        return memberRepository
            .save(Member.createMember(memberCreateDto, passwordEncoder))
            .flatMap(MemberRes::from);
    }
}
