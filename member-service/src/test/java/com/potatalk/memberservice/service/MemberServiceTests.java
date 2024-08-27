package com.potatalk.memberservice.service;

import static org.mockito.Mockito.*;

import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberCreateDto;
import com.potatalk.memberservice.dto.MemberRes;
import com.potatalk.memberservice.repository.MemberRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Nested
    class createMember {

        @Test
        void success() {
            // Arrange
            final MemberCreateDto request = new MemberCreateDto("username", "password",
                "nickName");

            Member member = Member.createMember(request, passwordEncoder);

            when(memberRepository.save(isA(Member.class))).thenReturn(Mono.just(member));
            // Act
            final Mono<MemberRes> result = memberService.createMember(request);

            // Assert
            StepVerifier.create(result)
                .expectNextMatches(memberRes -> memberRes.getUsername().equals("username"))
                .verifyComplete();

        }
    }
}
