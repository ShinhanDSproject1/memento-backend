package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;

import java.util.List;

/* 멘토 리뷰 조회 */
public interface ReviewCustomRepository {
    List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentorSeq, Long cursor, int limit, BaseStatus status);
}
