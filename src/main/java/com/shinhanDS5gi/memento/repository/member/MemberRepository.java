package com.shinhanDS5gi.memento.repository.member;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.shinhanDS5gi.memento.domain.member.MemberType;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository{

    // 관리자 페이지 전체 회원 조회하기
    @Override
    List<Member> findAllByIdAndLimitAndCursor(Integer limit, Long cursor, BaseStatus status);

    //회원 탈퇴
    //(memberSeq로 status가 ACTIVE 상태인지 조회)
    Optional<Member> findByMemberSeqAndStatus(Long memberSeq, BaseStatus status);

    //로그인
    //(멤버아이디와 멤버타입을 동시에 만족하는 멤버엔티티를 DB에서 찾는다.(멘토/멘티/관리자))
    Optional<Member> findByMemberIdAndMemberType(String memberId, MemberType type);

    //로그인 시 아이디 중복확인을 위해 작성
    boolean existsByMemberId(String memberId);

    // memberSeq, status, memberType 으로 멤버 찾기
    Optional<Member> findByMemberSeqAndStatusAndMemberType(Long memberSeq, BaseStatus status, MemberType memberType);
}
