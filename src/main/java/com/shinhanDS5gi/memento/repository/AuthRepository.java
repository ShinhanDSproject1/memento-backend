package com.shinhanDS5gi.memento.repository;


import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  AuthRepository extends JpaRepository<Member, Long>{

    // 아이디 타입 상태 ACTIVE 상태만 찾기
    Optional<Member> findByMemberIdAndMemberTypeAndStatus(String memberId, MemberType type, BaseStatus status);

    //jwt
    //로그인 시 username 기반 조회, RefreshToken 회전 시 계정 재확인
    Optional<Member> findByMemberId(String memberId);

    //로그인
    //member_id + member_type 으로 Member 조회(멘토/멘티/관리자)
    Optional<Member> findByMemberIdAndMemberType(String memberId, MemberType type);


}
