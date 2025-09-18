package com.shinhanDS5gi.memento.repository.review;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.mento.MentoReviewsListResponse;
import com.shinhanDS5gi.memento.dto.mentos.GetReviewListForMentosDetailResponse;

import java.util.List;

public interface ReviewCustomRepository {
    /* 멘토 리뷰 조회 */
    List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentoSeq, Long cursor, int limit, BaseStatus status);

    /* 멘토스 상세조회 */
    List<GetReviewListForMentosDetailResponse.Review> findReviewByMentosSeqAndLimitAndCursorAndStatus(Long mentosSeq, Integer limit, Long cursor, BaseStatus status);
}
