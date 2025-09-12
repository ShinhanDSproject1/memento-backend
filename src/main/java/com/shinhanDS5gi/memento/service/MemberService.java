package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.dto.auth.MentiSignupRequest;
import com.shinhanDS5gi.memento.dto.auth.MentoSignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService {


    // 관리자 페이지 전체 회원 조회하기
    GetMemberListResponse getMemberList(Integer limit, Long cursor);

    //회원탈퇴( AT블랙리스트 + RT 삭제 + 쿠키 삭제)
    void withdraw(Long memberSeq,HttpServletRequest req, HttpServletResponse res, boolean secureCookie);
  
    //회원가입 멘토
    void signupMento (MentoSignupRequest req, MultipartFile certImage, String idemKey) throws IOException;

    //회원가입 멘티
    void signupMenti (MentiSignupRequest req, String idemKey);

    //제명하기
    void expelMemberByAdmin(Long memberSeq);
}
