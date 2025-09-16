package com.shinhanDS5gi.memento.dto.mentos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetMentosDetailResponse {
    /**
     * 멘토스 상세조회 dto
     */
    private final String mentosImage;
    private final String mentosTitle;
    private final String mentosLocation;
    private final Integer reviewTotalCnt;
    private final Double reviewRatingAvg;
    private final List<Review> reviews;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Review{
        private final Long reviewSeq;
        private final Integer reviewRating;
        private final String reviewDate;
        private final String reviewContent;
    }

    private final MentoDetail mento;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MentoDetail{
        private final String mentoName;
        private final String mentoImg;
        private final String mentoDescription;
    }

    private final String mentosDescription;
    private final int mentosPrice;
}
