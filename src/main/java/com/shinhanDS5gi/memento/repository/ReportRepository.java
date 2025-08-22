package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /* 모든 신고 내역을 조회
        +1 문제를 해결하기 위해 fetch join을 사용하여 연관된 Member, Mentos 엔티티를 함께 조회 */
    @Query("SELECT r FROM Report r JOIN FETCH r.member m JOIN FETCH r.mentos mt")
    List<Report> findAllWithMemberAndMentos();
}
