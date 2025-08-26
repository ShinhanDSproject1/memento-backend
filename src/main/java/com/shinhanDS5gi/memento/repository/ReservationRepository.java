package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /* 멘토가 작성한 모든 멘토스의 예약 목록 조회 */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.mentos m JOIN FETCH r.member menti WHERE m.member.memberSeq = :mentorId AND r.status = 'ACTIVE' ORDER BY m.mentosSeq, r.mentosAt")
    List<Reservation> findAllByMentorId(@Param("mentorId") Long mentorId);
}
