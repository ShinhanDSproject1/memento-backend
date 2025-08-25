package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.MentoProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoProfileRepository extends JpaRepository<MentoProfile, Long> {

    /* memberSeq를 기준으로 멘토 프로필이 존재하는지 확인 */
    boolean existsByMember_MemberSeq(Long memberSeq);
}
