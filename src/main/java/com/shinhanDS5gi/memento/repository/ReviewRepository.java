package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Review;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
    
    /* 멘토 리뷰 조회 */
    @Override
    List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentorSeq, Long cursor, int limit, BaseStatus status);

    /* memberSeq와 mentosSeq로 이미 작성된 리뷰가 있는지 확인하는 메서드 */
    boolean existsByMember_MemberSeqAndMentos_MentosSeq(Long memberSeq, Long mentosSeq);
}
