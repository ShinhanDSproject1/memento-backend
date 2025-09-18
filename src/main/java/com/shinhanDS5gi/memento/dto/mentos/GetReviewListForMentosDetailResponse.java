package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetReviewListForMentosDetailResponse {

    /* 멘토스 상세보기에서 리뷰 무한 스크롤로 받아오기 위한 dto */

    private final List<Review> reviews;
    private final boolean hasNext;
    private final Long nextCursor;

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

