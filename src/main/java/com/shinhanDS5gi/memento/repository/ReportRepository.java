package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.domain.report.ReportHandleStatus;
import com.shinhanDS5gi.memento.domain.report.ReportType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /* PENDING 상태인 모든 신고 내역을 조회 (최신 순) */
    @Query("SELECT r FROM Report r " +
            "JOIN FETCH r.member m " +
            "JOIN FETCH r.mentos mt " +
            "WHERE r.reportSeq < :cursor AND r.handleStatus = :handleStatus " +
            "ORDER BY r.reportSeq DESC")
    List<Report> findReportsAfterCursor(@Param("cursor") Long cursor,
                                        @Param("handleStatus") ReportHandleStatus handleStatus,
                                        Pageable pageable);

    /* reportSeq를 기준으로 특정 신고 내역 조회 */
    @Query("SELECT r FROM Report r JOIN FETCH r.member m JOIN FETCH r.mentos mt WHERE r.reportSeq = :reportSeq")
    Optional<Report> findByIdWithMemberAndMentos(@Param("reportSeq") Long reportSeq);

    /* 같은 사유의 중복 신고 방지 메서드 */
    boolean existsByMember_MemberSeqAndMentos_MentosSeqAndReportTypeAndStatus(Long memberSeq, Long mentosSeq, ReportType reportType, BaseStatus status);

    @Modifying(clearAutomatically = true)
    @Query("update Report r set r.status = :afterStatus where r.member.memberSeq = :memberSeq and r.status = :beforeStatus")
    int updateReportStatus(@Param("memberSeq") Long memberSeq,
                           @Param("afterStatus") BaseStatus afterStatus,
                           @Param("beforeStatus") BaseStatus beforeStatus);

    boolean existsByMember_MemberSeqAndMentos_MentosSeqAndStatus(Long memberSeq, Long mentosSeq, BaseStatus status);
}