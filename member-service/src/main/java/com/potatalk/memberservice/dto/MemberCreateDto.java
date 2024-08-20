package com.potatalk.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberCreateDto {

    private String username;

    private String password;

    private String nickName;
}
