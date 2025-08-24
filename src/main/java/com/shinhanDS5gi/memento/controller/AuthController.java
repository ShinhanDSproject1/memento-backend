package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.LogoutRequest;
import com.shinhanDS5gi.memento.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")

public class AuthController {
    final MemberService memberService;
    //로그아웃
    @PostMapping("/logout")
    public BaseResponse<Void> logout(@RequestBody LogoutRequest request) {
        memberService.logout(request.getMemberSeq());
        return new BaseResponse<>(SUCCESS, null);
    }
}