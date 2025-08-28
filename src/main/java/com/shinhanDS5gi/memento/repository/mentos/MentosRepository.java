package com.shinhanDS5gi.memento.repository.mentos;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.mentos.GetMentosDetailProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentosRepository extends JpaRepository<Mentos, Long>, MentosCustomRepository {

    /* 접속한 멘토 유저의 활성화된 멘토스 목록 조회 */
    @Query("SELECT m FROM Mentos m WHERE m.member.memberSeq = :memberSeq AND m.status = :status AND m.mentosSeq < :cursor ORDER BY m.mentosSeq DESC")
    Slice<Mentos> findMyMentosSlice(@Param("memberSeq") Long memberSeq, @Param("cursor") Long cursor, @Param("status") BaseStatus status, Pageable pageable);

    /* 멘토스 전체조회(카테고리별) */
    @Modifying(clearAutomatically = true)
    @Query("update Mentos m set m.status = :afterStatus where m.member.memberSeq = :memberSeq and m.status = :beforeStatus")
    int updateMentosStatus(@Param("memberSeq") Long memberSeq,
                           @Param("afterStatus") BaseStatus afterStatus,
                           @Param("beforeStatus") BaseStatus beforeStatus);

    GetMentosDetailProjection findMentosDetailByMentosSeqAndStatus(Long mentosSeq, BaseStatus status);

    Optional<Mentos> findByMentosSeqAndStatus(Long mentosSeq, BaseStatus status);
}
