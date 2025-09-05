package com.shinhanDS5gi.memento.dto.mento;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/* 멘토 프로필 수정을 위한 요청 DTO */
@Getter
@NoArgsConstructor
public class UpdateMentoProfileRequest {

    @NotBlank(message = "멘토 소개 내용은 필수입니다.")
    private String mentoProfileContent;

    @NotNull(message = "시작 시간은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;

    @NotBlank(message = "진행 가능 요일은 필수입니다.")
    private String availableDays;

    @NotBlank(message = "우편번호는 필수입니다.")
    private String mentoPostcode;

    @NotBlank(message = "도로명 주소는 필수입니다.")
    private String mentoRoadAddress;

    @NotBlank(message = "법정동 이름은 필수입니다.")
    private String mentoBname;

    private String mentoDetail;
}
