package com.shinhanDS5gi.memento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
//로그인 Request DTO
public class LoginRequest {
    private String memberId;
    private String memberPwd;
}
