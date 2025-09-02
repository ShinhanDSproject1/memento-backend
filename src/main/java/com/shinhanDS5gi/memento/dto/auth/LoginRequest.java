package com.shinhanDS5gi.memento.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
//로그인 Request DTO
public class LoginRequest {
    private String memberId;
    private String memberPwd;
}
