package com.shinhanDS5gi.memento.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
//로그인 Request DTO
public class LoginRequest {
    private String memberId;
    private String memberPwd;
}
