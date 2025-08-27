package com.shinhanDS5gi.memento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/* 나의 프로필 정보(전화번호, 생년월일) 수정을 위한 요청 DTO */
@Getter
@NoArgsConstructor
public class UpdateMyProfileRequest {

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-XXXX-XXXX)")
    private String memberPhoneNumber;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate memberBirthDate;
}
