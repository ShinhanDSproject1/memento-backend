package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/* 멘토 자격증 저장용 */
public interface MentoCertificationRepository extends JpaRepository<MentoCertification, Long> {

    /* 내 보유 자격증 목록 조회 */
    List<MentoCertification> findAllByMember_MemberSeqAndStatus(Long memberSeq, BaseStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update MentoCertification mc set mc.status = :afterStatus where mc.member.memberSeq = :memberSeq and mc.status = :beforeStatus")
    int updateMentoCertificationStatus(@Param("memberSeq") Long memberSeq,
                                       @Param("afterStatus") BaseStatus afterStatus,
                                       @Param("beforeStatus") BaseStatus beforeStatus);
}
