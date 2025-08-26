package com.shinhanDS5gi.memento.dto.auth;

import lombok.*;

@NoArgsConstructor
@Getter
@AllArgsConstructor
//회원가입 멘티 관련 DTO
public class MentiSignupRequest {
    String memberId;
    String memberPwd;
    String memberName;
    String memberPhoneNumber;
    String memberBirthDate;


}
