package com.potatalk.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInDto {

    private String username;

    private String password;
}
