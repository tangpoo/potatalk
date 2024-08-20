package com.potatalk.memberservice.domain;

import com.potatalk.memberservice.dto.MemberCreateDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.crypto.password.PasswordEncoder;

@Table("members")
@Getter
@NoArgsConstructor()
public class Member {

    @Id
    private String id;

    private String username;

    private String password;

    private String nickName;

    private Member(MemberCreateDto memberCreateDto, PasswordEncoder passwordEncoder) {
        this.username = memberCreateDto.getUsername();
        this.password = passwordEncoder.encode(memberCreateDto.getPassword());
        this.nickName = memberCreateDto.getNickName();
    }

    public static Member createMember(MemberCreateDto memberCreateDto, PasswordEncoder passwordEncoder) {
        return new Member(memberCreateDto, passwordEncoder);
    }
}
