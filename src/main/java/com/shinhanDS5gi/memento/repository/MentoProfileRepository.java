package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MentoProfileRepository extends JpaRepository<MentoProfile, Long> {

    /* memberSeq를 기준으로 멘토 프로필이 존재하는지 확인 */
    boolean existsByMember_MemberSeq(Long memberSeq);

    @Modifying(clearAutomatically = true)
    @Query("update MentoProfile mp set mp.status = :afterStatus where mp.member.memberSeq = :memberSeq and mp.status = :beforeStatus")
    int updateMentoProfileStatus(@Param("memberSeq") Long memberSeq,
                                       @Param("afterStatus") BaseStatus afterStatus,
                                       @Param("beforeStatus") BaseStatus beforeStatus);

    /* memberSeq에 맞는 mentoProfile 조회 */
    Optional<MentoProfile> findByMember_MemberSeq(Long memberSeq);
}
