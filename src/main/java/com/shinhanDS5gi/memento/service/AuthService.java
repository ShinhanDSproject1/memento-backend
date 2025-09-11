package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    // 로그인 & 토큰 쿠키에(AT,RT) 발급
    Member issueTokens(MemberType type, LoginRequest req,
                       HttpServletResponse res, boolean secureCookie);

    // RT으로 AT 토큰 재발급
    void refresh(HttpServletRequest req, HttpServletResponse res, boolean secureCookie);

    // 로그인 (아이디와 멤버타입확인)-> 토큰 없는 단순 로그인
    Member login(MemberType pathType, LoginRequest request);

    // AT 블랙리스트 등록 + RT 삭제 + 쿠키 삭제)
    void cleanupTokensAndCookies(HttpServletRequest req, HttpServletResponse res, boolean secureCookie);

    // 로그아웃
    void logout(HttpServletRequest req, HttpServletResponse res, boolean secureCookie);
}
