package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.Modifying;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /* memberSeq와 mentosSeq, status를 기준으로 활성화된 예약을 조회하는 메서드 */
    Optional<Reservation> findByMember_MemberSeqAndMentos_MentosSeqAndStatus(Long memberSeq, Long mentosSeq, BaseStatus status);

    /* 멘토가 작성한 모든 멘토스의 예약 목록 조회 */
    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.mentos m " +
            "JOIN FETCH m.member mem " +
            "JOIN FETCH mem.mentoProfile mp " +
            "JOIN FETCH r.member menti " +
            "WHERE m.member.memberSeq = :mentorId AND r.status = 'ACTIVE' " +
            "ORDER BY m.mentosSeq, r.mentosAt")
    List<Reservation> findAllByMentorId(@Param("mentorId") Long mentorId);

    /* 특정 멘티의 멘토스 예약 목록을 커서 기반 페이징으로 조회 */
    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.mentos m " +
            "JOIN FETCH m.member mem " +
            "JOIN FETCH mem.mentoProfile mp " +
            "WHERE r.member.memberSeq = :memberSeq AND r.status = :reservationStatus AND m.status = :mentosStatus " +
            "ORDER BY " +
            "  CASE WHEN r.mentosAt > CURRENT_TIMESTAMP THEN 1 ELSE 2 END ASC, " +
            "  r.reservationSeq DESC")
    List<Reservation> findAllByMemberSeqAndStatusWithSorted(
            @Param("memberSeq") Long memberSeq,
            @Param("reservationStatus") BaseStatus reservationStatus,
            @Param("mentosStatus") BaseStatus mentosStatus
    );

    @Modifying(clearAutomatically = true)
    @Query("update Reservation r set r.status = :afterStatus where r.member.memberSeq = :memberSeq and r.status = :beforeStatus")
    int updateReservationStatus(@Param("memberSeq") Long memberSeq,
                                @Param("afterStatus") BaseStatus afterStatus,
                                @Param("beforeStatus") BaseStatus beforeStatus);

    @Query("""
        select r.mentosTime
        from Reservation r
        where r.mentos.mentosSeq = :mentosSeq
          and r.mentosAt = :mentosAt
          and r.status = :status
    """)
    List<LocalTime> findBookedTimes(@Param("mentosSeq") Long mentosSeq,
                                        @Param("mentosAt") LocalDate mentosAt,
                                        @Param("status") BaseStatus status);

    // 예약하기 : mentosSeq, mentosAt(날짜), mentosTime(시간), status 로 존재하는 Reservation 이 있는지 확인
    boolean existsByMentos_MentosSeqAndMentosAtAndMentosTimeAndStatus(Long mentosSeq, LocalDate mentosAt, LocalTime mentosTime, BaseStatus status);

    Optional<Reservation> findByReservationSeqAndStatus(Long reservationSeq, BaseStatus baseStatus);

    boolean existsByMentos_MentosSeqAndStatus(Long mentosSeq, BaseStatus status);
}
