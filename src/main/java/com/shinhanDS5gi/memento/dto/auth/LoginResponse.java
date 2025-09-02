package com.shinhanDS5gi.memento.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
//로그인 Response DTO
public class LoginResponse {
    private String memberName;
    private String memberType;

}
