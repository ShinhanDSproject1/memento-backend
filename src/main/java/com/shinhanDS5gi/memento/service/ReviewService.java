package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateReviewRequest;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewService {

    /* 리뷰 작성하기 */
    @Transactional
    void createReview(Long memberSeq, CreateReviewRequest requestDTO);
}
