package com.shinhanDS5gi.memento.repository.review;

import com.shinhanDS5gi.memento.domain.Review;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update Review r
       set r.status = :afterStatus
     where r.status = :beforeStatus
       and r.reservation.reservationSeq in (
            select rs.reservationSeq
              from Reservation rs
             where rs.member.memberSeq = :memberSeq
               and rs.status = :beforeStatus
       )
""")
    int updateReviewStatus(
            @Param("memberSeq") Long memberSeq,
            @Param("afterStatus") BaseStatus afterStatus,
            @Param("beforeStatus") BaseStatus beforeStatus);



}
