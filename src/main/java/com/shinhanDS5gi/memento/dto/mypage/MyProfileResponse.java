package com.shinhanDS5gi.memento.dto.mypage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shinhanDS5gi.memento.dto.mento.MentoCertificationsResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/* 나의 프로필 조회를 위한 응답 DTO*/
@Getter
@Builder
public class MyProfileResponse {

    private final String memberName;
    private final String memberPhoneNumber;
    private final LocalDate memberBirthDate;
    private final String memberId;
    private final String memberType;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 리스트가 비어있으면 JSON 응답에서 제외
    private final List<MentoCertificationsResponse> certifications;
}
