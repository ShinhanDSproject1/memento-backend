package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MentosRepository extends JpaRepository<Mentos, Long> {

    /* 접속한 멘토 유저의 활성화된 멘토스 목록 조회 */
    @Query("SELECT m FROM Mentos m WHERE m.member.memberSeq = :memberSeq AND m.status = :status AND m.mentosSeq < :cursor ORDER BY m.mentosSeq DESC")
    Slice<Mentos> findMyMentosSlice(@Param("memberSeq") Long memberSeq, @Param("cursor") Long cursor, @Param("status") BaseStatus status, Pageable pageable);
}
