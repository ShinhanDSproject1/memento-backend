package com.shinhanDS5gi.memento.dto;

import lombok.*;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
//회원가입 멘티 관련 DTO
public class MentiSignupRequest {
    String memberId;
    String memberPwd;
    String memberName;
    String memberPhoneNumber;
    String memberBirthDate;


}
