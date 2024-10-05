package com.potatalk.memberservice.controller;

import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.dto.MemberUpdateDto;
import com.potatalk.memberservice.dto.SignInDto;
import com.potatalk.memberservice.dto.SingUpDto;
import com.potatalk.memberservice.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<MemberRes>> createMember(@RequestBody SingUpDto memberCreateDto) {
        return memberService
                .createMember(memberCreateDto)
                .map(memberRes -> ResponseEntity.status(HttpStatus.CREATED).body(memberRes));
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<Void>> login(@RequestBody SignInDto signInDto) {
        return memberService
                .signIn(signInDto)
                .map(token -> ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).build());
    }

    @PutMapping
    public Mono<ResponseEntity<MemberRes>> updateMember(
            @RequestBody MemberUpdateDto memberUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return memberService
                .updateMember(memberUpdateDto, userDetails.getUsername())
                .map(res -> ResponseEntity.ok().body(res));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails) {
        memberService.deleteMember(userDetails.getUsername());

        return Mono.just(ResponseEntity.noContent().build());
    }

    @GetMapping("/{memberId}")
    public Mono<ResponseEntity<MemberRes>> findMember(@PathVariable Long memberId) {
        return memberService.findMember(memberId).map(res -> ResponseEntity.ok().body(res));
    }

    @PostMapping("/friend/{friendId}")
    public Mono<ResponseEntity<Void>> friendRequest(
            @RequestHeader(required = false, value = "X-Username") String username,
            @PathVariable Long friendId) {
        log.info("init controller with: " + username);
        memberService.friendRequest(username, friendId);

        return Mono.just(ResponseEntity.noContent().build());
    }

    @GetMapping("/friend")
    public Flux<MemberRes> findAllFriend(
            @RequestHeader(required = false, value = "X-Username") String username) {
        log.info("init controller with: " + username);
        return memberService.findAllFriend(username);
    }

    @PostMapping("/friend/{friendId}/accept")
    public Mono<ResponseEntity<Void>> acceptFriendRequest(
            @RequestHeader(required = false, value = "X-Username") String username,
            @PathVariable Long friendId) {
        memberService.acceptFriendRequest(username, friendId);

        return Mono.just(ResponseEntity.noContent().build());
    }
}
