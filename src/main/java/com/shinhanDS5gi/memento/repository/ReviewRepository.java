package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Review;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.MentoReviewsListResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    /* 멘토 리뷰 조회 */
    @Override
    List<MentoReviewsListResponse> findMentoReviewsByCursor(Long mentorSeq, Long cursor, int limit, BaseStatus status);

    /* memberSeq와 mentosSeq로 이미 작성된 리뷰가 있는지 확인하는 메서드 */
    boolean existsByMember_MemberSeqAndMentos_MentosSeq(Long memberSeq, Long mentosSeq);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Review r set r.status = :afterStatus where r.member.memberSeq = :memberSeq and r.status = :beforeStatus")
    int updateReviewStatus(@Param("memberSeq") Long memberSeq,
                           @Param("afterStatus") BaseStatus afterStatus,
                           @Param("beforeStatus") BaseStatus beforeStatus);


}
