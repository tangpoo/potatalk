package com.potatalk.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateDto {

    private String username;
    private String nickName;
}
