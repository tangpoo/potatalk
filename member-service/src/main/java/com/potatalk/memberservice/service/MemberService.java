package com.potatalk.memberservice.service;

import brave.internal.collect.UnsafeArrayMap.Mapper;
import com.potatalk.memberservice.config.jwt.JwtTokenProvider;
import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberUpdateDto;
import com.potatalk.memberservice.dto.SignInDto;
import com.potatalk.memberservice.dto.SingUpDto;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<MemberRes> createMember(final SingUpDto singUpDto) {
        return memberRepository
            .save(Member.createMember(singUpDto, passwordEncoder))
            .flatMap(MemberRes::from);
    }

    public Mono<String> signIn(final SignInDto signInDto) {
        return memberRepository
            .findByUsername(signInDto.getUsername())
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
            .flatMap(member -> MemberValidator.validatePassword(member, signInDto.getPassword(), passwordEncoder))
            .flatMap(member -> jwtTokenProvider.createToken(member.getUsername()));
    }

    public Mono<MemberRes> updateMember(final MemberUpdateDto memberUpdateDto, final String username) {
        return memberRepository.
            findByUsername(username)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
            .flatMap(member -> member.update(memberUpdateDto))
            .flatMap(MemberRes::from);
    }

    public void deleteMember(final String username) {
        memberRepository
            .findByUsername(username)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
            .doOnSuccess(memberRepository::delete)
            .subscribe();
    }

    private static class MemberValidator {

        public static Mono<Member> validatePassword(Member member, String password, PasswordEncoder passwordEncoder) {
            return member.passwordMatch(password, passwordEncoder)
                ? Mono.just(member)
                : Mono.error(new AccessDeniedException("패스워드가 일치하지 않습니다."));
        }
    }
}
