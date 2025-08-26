package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Review r set r.status = :afterStatus where r.member.memberSeq = :memberSeq and r.status = :beforeStatus")
    int updateReviewStatus(@Param("memberSeq") Long memberSeq,
                           @Param("afterStatus") BaseStatus afterStatus,
                           @Param("beforeStatus") BaseStatus beforeStatus);
}
