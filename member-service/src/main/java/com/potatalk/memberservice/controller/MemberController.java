package com.potatalk.memberservice.controller;

import com.potatalk.memberservice.dto.MemberCreateDto;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public Mono<MemberRes> createMember(@RequestBody MemberCreateDto memberCreateDto) {
        return memberService.createMember(memberCreateDto);
    }
}
