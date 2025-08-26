package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.LoginRequest;
import com.shinhanDS5gi.memento.dto.MentiSignupRequest;
import com.shinhanDS5gi.memento.dto.MentoSignupRequest;

public interface MemberService {
  
    // 관리자 페이지 전체 회원 조회하기
    GetMemberListResponse getMemberList(Integer limit, Long cursor);

    //회원탈퇴
    void withdraw(Long memberSeq);

    //로그인 (아이디와 멤버타입확인)
    Member login(MemberType pathType, LoginRequest request);
    
    //로그아웃
    void logout(Long memberSeq);
  
    //회원가입 멘토
    void signupMento (MentoSignupRequest req);

    //회원가입 멘티
    void signupMenti (MentiSignupRequest req);

}
