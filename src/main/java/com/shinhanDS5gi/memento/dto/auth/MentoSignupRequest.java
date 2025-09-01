package com.shinhanDS5gi.memento.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
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
