package com.potatalk.memberservice.domain;

import com.potatalk.memberservice.dto.MemberUpdateDto;
import com.potatalk.memberservice.dto.SingUpDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.crypto.password.PasswordEncoder;

@Table("members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id private Long id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("nick_name")
    private String nickName;

    private Member(SingUpDto singUpDto, PasswordEncoder passwordEncoder) {
        this.username = singUpDto.getUsername();
        this.password = passwordEncoder.encode(singUpDto.getPassword());
        this.nickName = singUpDto.getNickName();
    }

    public static Member createMember(SingUpDto singUpDto, PasswordEncoder passwordEncoder) {
        return new Member(singUpDto, passwordEncoder);
    }

    public boolean passwordMatch(final String password, final PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    public Member update(final MemberUpdateDto memberUpdateDto) {
        this.username = memberUpdateDto.getUsername();
        this.nickName = memberUpdateDto.getNickName();
        return this;
    }
}
