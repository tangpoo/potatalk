package com.potatalk.memberservice.controller;

import com.potatalk.memberservice.config.jwt.JwtTokenProvider;
import com.potatalk.memberservice.dto.SignInDto;
import com.potatalk.memberservice.dto.SingUpDto;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/signup")
    public Mono<ResponseEntity<MemberRes>> createMember(@RequestBody SingUpDto memberCreateDto) {
        return memberService.createMember(memberCreateDto)
            .map(memberRes -> ResponseEntity.status(HttpStatus.CREATED).body(memberRes));
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<Void>> login(@RequestBody SignInDto signInDto) {
        return memberService.signIn(signInDto)
            .map(token -> ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .build());
    }
}
