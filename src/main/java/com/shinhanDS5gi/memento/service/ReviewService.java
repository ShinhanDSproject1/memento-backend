package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.mento.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.mento.MentoReviewsSliceResponse;
import com.shinhanDS5gi.memento.dto.mypage.CreateReviewRequest;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewService {

    /* 멘토 리뷰 조회 */
    MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor);

    /* 리뷰 작성하기 */
    @Transactional
    void createReview(Long memberSeq, CreateReviewRequest requestDTO, String idemKey);
}
