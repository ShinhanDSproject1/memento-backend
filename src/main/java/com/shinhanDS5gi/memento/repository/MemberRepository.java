package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //로그인 시 아이디 중복확인을 위해 작성
    boolean existsByMemberId(String memberId);
}
