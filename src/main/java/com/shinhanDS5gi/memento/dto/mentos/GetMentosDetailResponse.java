package com.shinhanDS5gi.memento.dto.mentos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetMentosDetailResponse {
    /**
     * 멘토스 상세조회 dto
     */

    // Mentos 정보
    private final String mentosImage;
    private final String mentosTitle;
    private final String mentosDescription;
    private final int mentosPrice;

    // MentoProfile 정보 (장소 및 시간)
    private final String mentoPostcode;
    private final String mentoRoadAddress;
    private final String mentoBname;
    private final String mentoDetailAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime endTime;
    private final String availableDays;

    // Mento 정보
    private final MentoDetail mento;

    // Review 정보
    private final Integer reviewTotalCnt;
    private final Double reviewRatingAvg;
    private final List<Review> reviews;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MentoDetail {
        private final String mentoName;
        private final String mentoImg;
        private final String mentoDescription;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Review{
        private final Long reviewSeq;
        private final Integer reviewRating;
        private final String reviewDate;
        private final String reviewContent;
    }
}
