package com.shinhanDS5gi.memento.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
// 회원가입 멘토 DTO
public class MentoSignupRequest {
    private String memberId;
    private String memberPwd;
    private String memberName;
    private String memberPhoneNumber;
    private String memberBirthDate;

    private List<MentoCertificationRequest> certification; // 멘토 자격증

}
