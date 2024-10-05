package com.potatalk.memberservice.service;

import com.potatalk.memberservice.config.jwt.JwtTokenProvider;
import com.potatalk.memberservice.domain.Friend;
import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.dto.MemberUpdateDto;
import com.potatalk.memberservice.dto.SignInDto;
import com.potatalk.memberservice.dto.SingUpDto;
import com.potatalk.memberservice.repository.FriendRepository;
import com.potatalk.memberservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<MemberRes> createMember(final SingUpDto singUpDto) {
        return memberRepository
                .save(Member.createMember(singUpDto, passwordEncoder))
                .map(MemberRes::from);
    }

    public Mono<String> signIn(final SignInDto signInDto) {
        return memberRepository
                .findByUsername(signInDto.getUsername())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .flatMap(
                        member ->
                                MemberValidator.validatePassword(
                                        member, signInDto.getPassword(), passwordEncoder))
                .flatMap(member -> jwtTokenProvider.createToken(member.getUsername()));
    }

    public Mono<MemberRes> updateMember(
            final MemberUpdateDto memberUpdateDto, final String username) {
        return memberRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(member -> member.update(memberUpdateDto))
                .flatMap(memberRepository::save)
                .map(MemberRes::from);
    }

    public void deleteMember(final String username) {
        memberRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .doOnSuccess(memberRepository::delete)
                .subscribe();
    }

    public Mono<MemberRes> findMember(final Long memberId) {
        return memberRepository
                .findById(memberId)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(MemberRes::from);
    }

    public void friendRequest(String username, final Long friendId) {
        log.info("username:" + username);
        memberRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .flatMap(
                        member ->
                                memberRepository
                                        .existsById(friendId)
                                        .flatMap(
                                                friendExists -> {
                                                    if (!friendExists) {
                                                        return Mono.error(
                                                                new IllegalArgumentException(
                                                                        "friend not found"));
                                                    }
                                                    final Friend friend =
                                                            Friend.create(member.getId(), friendId);
                                                    return friendRepository.save(friend);
                                                }))
                .subscribe();
    }

    public Flux<MemberRes> findAllFriend(final String username) {
        return memberRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .flatMapMany(
                        member ->
                                friendRepository
                                        .findAllFriendsByMemberId(member.getId())
                                        .map(
                                                friend ->
                                                        friend.getMemberId().equals(member.getId())
                                                                ? friend.getFriendId()
                                                                : friend.getMemberId())
                                        .collectList()
                                        .flatMapMany(memberRepository::findAllById)
                                        .map(MemberRes::from));
    }

    public void acceptFriendRequest(final String username, final Long friendId) {
        memberRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .flatMap(
                        member ->
                                friendRepository.findByMemberIdAndFriendId(
                                        member.getId(), friendId))
                .switchIfEmpty(Mono.error(new ClassNotFoundException()))
                .flatMap(friend -> friendRepository.save(friend.accept()))
                .then()
                .subscribe();

        // member 찾기 - memberId와 friendId로 friend 찾기 - friend 상태 변경 - save
    }

    private static class MemberValidator {

        public static Mono<Member> validatePassword(
                Member member, String password, PasswordEncoder passwordEncoder) {
            return member.passwordMatch(password, passwordEncoder)
                    ? Mono.just(member)
                    : Mono.error(new AccessDeniedException("패스워드가 일치하지 않습니다."));
        }
    }
}
