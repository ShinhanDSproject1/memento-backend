package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

/* 멘토 리뷰 조회 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

}