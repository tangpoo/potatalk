package com.potatalk.memberservice.domain;

import com.potatalk.memberservice.steps.MemberSteps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MemberTests {

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Nested
    class Member_password_matches {

        @Test
        void same_password() {
            // Arrange
            final Member member = MemberSteps.createMember();
            String requestPassword = "password-1234";

            // Act
            final boolean isMatches = member.passwordMatch(requestPassword, passwordEncoder);

            // Assert
            Assertions.assertThat(isMatches).isTrue();
        }
    }
}
