package com.potatalk.memberservice.steps;

import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.SingUpDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MemberSteps {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static Member createMember() {
        final SingUpDto request = new SingUpDto(
            "username-1234",
            "password-1234",
            "nickName-1234"
        );

        return Member.createMember(request, passwordEncoder);
    }

    public static Member createMemberWithUsername(String username) {
        final SingUpDto request = new SingUpDto(
            username,
            "password-1234",
            "nickName-1234"
        );

        return Member.createMember(request, passwordEncoder);
    }
}
