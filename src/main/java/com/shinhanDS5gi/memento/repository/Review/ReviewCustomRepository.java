package com.shinhanDS5gi.memento.repository.Review;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosDetailResponse;

import java.util.List;

public interface ReviewCustomRepository {
    /* 멘토 리뷰 조회 */
    List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentorSeq, Long cursor, int limit, BaseStatus status);

    /* 멘토스 상세조회 */
    List<GetMentosDetailResponse.Review> findReviewByMentosSeqAndStatus(Long mentosSeq, BaseStatus status);
}
