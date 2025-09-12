package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.*;
import com.shinhanDS5gi.memento.service.AuthService;
import com.shinhanDS5gi.memento.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final AuthService  authService;

    /* 로그아웃 */
    //AT 블랙리스트 등록 + RT 삭제 + 쿠키 제거
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        boolean secureCookie = req.isSecure();
        authService.logout(req, res, secureCookie);
        return new BaseResponse<>(SUCCESS, null);
    }


    /* 로그인 */
    //비번검증 → AT/RT 발급 → Redis RT 저장 → Set-Cookie(AT/RT) 처리
    @PostMapping("/login/{user-type}")
    public BaseResponse<LoginResponse> login(
            @PathVariable("user-type") MemberType type,
            @RequestBody LoginRequest request,
            HttpServletRequest req,
            HttpServletResponse res) {
        boolean secure = req.isSecure();

        Member m = authService.issueTokens(type, request, res, secure);
        return new BaseResponse<>(SUCCESS,
                new LoginResponse(m.getMemberName(), m.getMemberType().name()));
    }

  }
