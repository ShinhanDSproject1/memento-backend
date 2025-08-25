package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.MentiSignupRequest;
import com.shinhanDS5gi.memento.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup/menti")
    public BaseResponse<Void> signupMenti(@RequestBody MentiSignupRequest requestDto) {

        // 멘티 회원가입 서비스 호출
        memberService.signupMenti(requestDto);

        // 성공 응답 리턴
        return new BaseResponse<>(SUCCESS, null);
    }
}
