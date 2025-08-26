package com.shinhanDS5gi.memento.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/* 나의 프로필 조회를 위한 응답 DTO*/
@Getter
@Builder
public class MyProfileResponse {

    private final String memberName;
    private final String memberPhoneNumber;
    private final LocalDate memberBirthDate;
    private final String memberId;
}
