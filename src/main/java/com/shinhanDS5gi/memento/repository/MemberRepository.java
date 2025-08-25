package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //로그인
    //멤버아이디와 멤버타입을 동시에 만족하는 멤버엔티티를 DB에서 찾는다.(멘토/멘티/관리자)
    Optional<Member> findByMemberIdAndMemberType(String memberId, MemberType type);

    //로그인 시 아이디 중복확인을 위해 작성
    boolean existsByMemberId(String memberId);
}
