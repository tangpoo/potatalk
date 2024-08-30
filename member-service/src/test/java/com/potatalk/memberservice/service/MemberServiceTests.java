package com.potatalk.memberservice.service;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.when;

import com.potatalk.memberservice.config.jwt.JwtTokenProvider;
import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.dto.SignInDto;
import com.potatalk.memberservice.dto.SingUpDto;
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
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Nested
    class createMember {

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
                .expectNextMatches(memberRes -> memberRes.getUsername().equals("username-1234"))
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
            final Member member = MemberSteps.createMember();
            String token = "token-1234";

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


}
