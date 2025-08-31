package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.MentoReviewsSliceResponse;
import com.shinhanDS5gi.memento.dto.CreateReviewRequest;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewService {

    /* 신고 거부하기 */
    void rejectionReport(Long reportSeq);

    /* 멘토 리뷰 조회 */
    MentoReviewsSliceResponse<MentoReviewsListResponse> getMentoReviews(Long mentorSeq, int limit, Long cursor);

    /* 리뷰 작성하기 */
    @Transactional
    void createReview(Long memberSeq, CreateReviewRequest requestDTO);
}
