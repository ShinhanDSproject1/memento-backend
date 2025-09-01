package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.Modifying;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /* memberSeq와 mentosSeq, status를 기준으로 활성화된 예약을 조회하는 메서드 */
    Optional<Reservation> findByMember_MemberSeqAndMentos_MentosSeqAndStatus(Long memberSeq, Long mentosSeq, BaseStatus status);

    /* 멘토가 작성한 모든 멘토스의 예약 목록 조회 */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.mentos m JOIN FETCH r.member menti WHERE m.member.memberSeq = :mentorId AND r.status = 'ACTIVE' ORDER BY m.mentosSeq, r.mentosAt")
    List<Reservation> findAllByMentorId(@Param("mentorId") Long mentorId);

    /* 특정 멘티의 멘토스 예약 목록을 커서 기반 페이징으로 조회 */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.mentos m WHERE r.member.memberSeq = :memberSeq AND r.status = :status AND r.reservationSeq < :cursor ORDER BY r.reservationSeq DESC")
    Slice<Reservation> findByMemberSeqWithSlice(@Param("memberSeq") Long memberSeq, @Param("cursor") Long cursor, @Param("status") BaseStatus status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Reservation r set r.status = :afterStatus where r.member.memberSeq = :memberSeq and r.status = :beforeStatus")
    int updateReservationStatus(@Param("memberSeq") Long memberSeq,
                                @Param("afterStatus") BaseStatus afterStatus,
                                @Param("beforeStatus") BaseStatus beforeStatus);
}
