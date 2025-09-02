package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.*;
import com.shinhanDS5gi.memento.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;

    /* 회원 탈퇴 */
    @PatchMapping("/member/{memberSeq}")
    public BaseResponse<Void> withdraw(@PathVariable Long memberSeq) {
        memberService.withdraw(memberSeq);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 로그아웃 */
    @PostMapping("/logout")
    public BaseResponse<Void> logout(@RequestBody LogoutRequest request) {
        memberService.logout(request.getMemberSeq());
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 로그인 */
    @PostMapping("/login/{user-type}")
    // 페이지 넘기는 값(URL의 {user-type} 값)이 enum의 정확한 이름과 동일하면  MemberType으로 바인딩
    public BaseResponse<LoginResponse> login(@PathVariable("user-type") MemberType type,
                                             @RequestBody LoginRequest request) {
        Member member = memberService.login(type, request);
        return new BaseResponse<>(SUCCESS, new LoginResponse(member.getMemberName(),member.getMemberType().name()));
    }

    /* 회원가입 (멘토) */
    @PostMapping(value = "/signup/mento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> signupMento(
            @RequestPart("requestDto") @Valid MentoSignupRequest requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile certImage
    ) throws IOException {
        memberService.signupMento(requestDto, certImage);
        return new BaseResponse<>(SUCCESS, null);
    }


    /* 회원가입 (멘티) */
    @PostMapping("/signup/menti")
    public BaseResponse<Void> signupMenti(@RequestBody MentiSignupRequest requestDto) {
        memberService.signupMenti(requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }
}
