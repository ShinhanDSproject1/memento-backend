package com.shinhanDS5gi.memento.dto.mento;

import com.shinhanDS5gi.memento.domain.MentoCertification;
import lombok.Builder;
import lombok.Getter;

/* 멘토의 보유 자격증 목록 조회를 위한 응답 DTO */
@Getter
@Builder
public class MentoCertificationsResponse {

    private final Long mentoCertificationSeq;
    private final String certificationName;
    private final String certificationImageUrl;

    public static MentoCertificationsResponse from(MentoCertification certification) {
        return MentoCertificationsResponse.builder()
                .mentoCertificationSeq(certification.getMentoCertificationSeq())
                .certificationName(certification.getMentoCertificationName())
                .certificationImageUrl(certification.getMentoCertificationImage())
                .build();
    }
}