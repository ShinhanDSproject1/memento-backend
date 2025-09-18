package com.shinhanDS5gi.memento.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
// 회원가입 멘토 DTO
public class MentoSignupRequest {
    private String memberId;
    private String memberPwd;
    private String memberName;
    private String memberPhoneNumber;
    private String memberBirthDate;

    private String certificationName;  // 멘토 자격증
    private String certificationImgUrl;

    private MultipartFile mentoImage; //멘토 프로필
    private String mentoProfileContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;

    private String availableDays;
    private String mentoPostcode;
    private String mentoRoadAddress;
    private String mentoBname;
    private String mentoDetail;
}
