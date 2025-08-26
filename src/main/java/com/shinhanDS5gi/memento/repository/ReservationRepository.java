package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /* memberSeq와 mentosSeq로 예약을 조회하는 메서드 */
    Optional<Reservation> findByMember_MemberSeqAndMentos_MentosSeq(Long memberSeq, Long mentosSeq);

    /* 멘토가 작성한 모든 멘토스의 예약 목록 조회 */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.mentos m JOIN FETCH r.member menti WHERE m.member.memberSeq = :mentorId AND r.status = 'ACTIVE' ORDER BY m.mentosSeq, r.mentosAt")
    List<Reservation> findAllByMentorId(@Param("mentorId") Long mentorId);
}
