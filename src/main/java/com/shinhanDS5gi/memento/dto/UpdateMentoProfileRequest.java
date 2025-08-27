package com.shinhanDS5gi.memento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 멘토 프로필 수정을 위한 요청 DTO */
@Getter
@NoArgsConstructor
public class UpdateMentoProfileRequest {

    @NotBlank(message = "멘토 소개 내용은 필수입니다.")
    private String mentoProfileContent;

    @NotBlank(message = "프로필 이미지는 필수입니다.")
    private String mentoProfileImage;
}
