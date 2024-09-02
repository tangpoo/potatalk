package com.potatalk.memberservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.potatalk.memberservice.config.jwt.JwtTokenProvider;
import com.potatalk.memberservice.domain.Friend;
import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.dto.MemberUpdateDto;
import com.potatalk.memberservice.dto.SignInDto;
import com.potatalk.memberservice.dto.SingUpDto;
import com.potatalk.memberservice.repository.FriendRepository;
import com.potatalk.memberservice.repository.MemberRepository;
import com.potatalk.memberservice.steps.MemberSteps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Nested
    class create_member {

        @Test
        void success() {
            // Arrange
            final SingUpDto request = new SingUpDto("username-1234", "password-1234",
                "nickName-1234");

            final Member member = MemberSteps.createMember();

            when(memberRepository.save(isA(Member.class))).thenReturn(Mono.just(member));
            // Act
            final Mono<MemberRes> result = memberService.createMember(request);

            // Assert
            StepVerifier.create(result)
                .expectNextMatches(memberRes -> memberRes.getUsername().equals(request.getUsername()))
                .verifyComplete();

        }
    }

    @Nested
    class login {

        @Test
        void success() {
            // Arrange
            final SignInDto signInDto = new SignInDto("username-1234", "password-1234");
            final Member member = MemberSteps.createMember();
            String token = "token-1234";

            when(memberRepository.findByUsername(anyString())).thenReturn(Mono.just(member));
            when(jwtTokenProvider.createToken(anyString())).thenReturn(Mono.just(token));

            // Act
            final Mono<String> result = memberService.signIn(signInDto);

            // Assert
            StepVerifier.create(result)
                .expectNext(token)
                .verifyComplete();
        }

        @Test
        void not_found_member() {
            // Arrange
            final SignInDto signInDto = new SignInDto("username-1234", "password-1234");

            when(memberRepository.findByUsername(anyString())).thenReturn(Mono.empty());

            // Act
            final Mono<String> result = memberService.signIn(signInDto);

            // Assert
            StepVerifier.create(result).expectError(UsernameNotFoundException.class).verify();
        }

        @Test
        void diff_password() {
            // Arrange
            String diffPassword = "password-4321";
            final SignInDto signInDto = new SignInDto("username-1234", diffPassword);
            final Member member = MemberSteps.createMember();
            String token = "token-1234";

            when(memberRepository.findByUsername(anyString())).thenReturn(Mono.just(member));

            // Act
            final Mono<String> result = memberService.signIn(signInDto);

            // Assert
            StepVerifier.create(result).expectError(AccessDeniedException.class).verify();
        }
    }

    @Test
    void update_member_success() {
        // Arrange
        final String username = "username-1234";
        final MemberUpdateDto memberUpdateDto = new MemberUpdateDto("username-update",
            "nickName-update");
        final Member member = MemberSteps.createMember();

        when(memberRepository.findByUsername(anyString())).thenReturn(Mono.just(member));
        when(memberRepository.save(any(Member.class))).thenReturn(Mono.just(member));

        // Act
        final Mono<MemberRes> result = memberService.updateMember(memberUpdateDto,
            username);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(res ->
                res.getUsername().equals(memberUpdateDto.getUsername()) &&
                    res.getNickName().equals(memberUpdateDto.getNickName())
            )
            .verifyComplete();
    }

    @Test
    void delete_member_success() {
        // Arrange
        String username = "username-1234";
        Member member = MemberSteps.createMember();

        when(memberRepository.findByUsername(anyString())).thenReturn(Mono.just(member));
        // Act
        memberService.deleteMember(username);

        // Assert
        verify(memberRepository, times(1)).delete(member);
    }

    @Nested
    class friend_request {

        @Test
        void success() {
            // Arrange
            String username = "username-1234";
            Long friendId = 1L;
            Long memberId = 2L;
            final Friend friend = Friend.create(memberId, friendId);
            final Member member = MemberSteps.createMember();

            when(memberRepository.findByUsername(username)).thenReturn(Mono.just(member));
            when(memberRepository.existsById(friendId)).thenReturn(Mono.just(true));
            when(friendRepository.save(any(Friend.class))).thenReturn(Mono.just(friend));

            // Act
            memberService.friendRequest(username, friendId);

            // Assert
            verify(memberRepository, times(1)).findByUsername(username);
            verify(memberRepository, times(1)).existsById(friendId);
            verify(friendRepository, times(1)).save(any(Friend.class));
        }

        @Test
        void not_exist_member() {
            // Arrange
            String username = "username-1234";
            Long friendId = 1L;
            final Member member = MemberSteps.createMember();

            when(memberRepository.findByUsername(username)).thenReturn(Mono.just(member));
            when(memberRepository.existsById(friendId)).thenReturn(Mono.empty());

            // Act
            memberService.friendRequest(username, friendId);

            // Assert
            verify(memberRepository, times(1)).findByUsername(username);
            verify(memberRepository, times(1)).existsById(friendId);
            verify(friendRepository, times(0)).save(any(Friend.class));
        }
    }

}
