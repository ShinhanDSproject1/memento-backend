package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.LoginRequest;
import com.shinhanDS5gi.memento.dto.auth.MentiSignupRequest;
import com.shinhanDS5gi.memento.dto.auth.MentoSignupRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    void signupMento (MentoSignupRequest req, MultipartFile certImage, String idemKey)throws IOException;

    //회원가입 멘티
    void signupMenti (MentiSignupRequest req);

    //제명하기
    void expelMemberByAdmin(Long memberSeq);
}
