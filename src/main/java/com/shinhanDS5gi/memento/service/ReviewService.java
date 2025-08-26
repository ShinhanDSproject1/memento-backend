package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.MentoReviewsSliceResponse;

public interface ReviewService {
    /* 멘토 리뷰 조회 */
    MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor);
}


