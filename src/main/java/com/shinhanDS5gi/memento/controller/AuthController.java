package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.*;
import com.shinhanDS5gi.memento.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;

    /**
     * 회원 탈퇴
     */
    @PatchMapping("/member/{memberSeq}")
    public BaseResponse<Void> withdraw(@PathVariable Long memberSeq) {
        memberService.withdraw(memberSeq);
        return new BaseResponse<>(SUCCESS, null);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public BaseResponse<Void> logout(@RequestBody LogoutRequest request) {
        memberService.logout(request.getMemberSeq());
        return new BaseResponse<>(SUCCESS, null);
    }

    /**
     * 로그인
     */
    @PostMapping("/login/{user-type}")
    // 페이지 넘기는 값(URL의 {user-type} 값)이 enum의 정확한 이름과 동일하면  MemberType으로 바인딩
    public BaseResponse<LoginResponse> login(@PathVariable("user-type") MemberType type,
                                             @RequestBody LoginRequest request) {
        //로그인 서비스 호출
        Member member = memberService.login(type, request);
        //성공 응답 리턴
        return new BaseResponse<>(SUCCESS, new LoginResponse(member.getMemberName()));
    }

    /**
     * 멘토 회원가입
     */
    @PostMapping("/signup/mento")
    public BaseResponse<Void> signupMento(@RequestBody MentoSignupRequest requestDto) {

        // 멘토 회원가입 서비스 호출
        memberService.signupMento(requestDto);
      
        // 성공 응답 리턴
        return new BaseResponse<>(SUCCESS, null);
    }

    /**
     * 멘티 회원가입
     */
    @PostMapping("/signup/menti")
    public BaseResponse<Void> signupMenti(@RequestBody MentiSignupRequest requestDto) {

        // 멘티 회원가입 서비스 호출
        memberService.signupMenti(requestDto);

        // 성공 응답 리턴
        return new BaseResponse<>(SUCCESS, null);
    }
}
