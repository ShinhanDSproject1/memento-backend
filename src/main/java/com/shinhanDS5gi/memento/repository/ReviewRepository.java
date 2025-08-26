package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/* 멘토 리뷰 조회 */
public interface ReviewRepository extends JpaRepository<Review, Long>,ReviewCustomRepository {
    @Override
    List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentorSeq, Long cursor, int limit, BaseStatus status);

}