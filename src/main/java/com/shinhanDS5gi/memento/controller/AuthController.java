package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.LoginRequest;
import com.shinhanDS5gi.memento.dto.LoginResponse;
import com.shinhanDS5gi.memento.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    final MemberService memberService;

    //로그인
    @PostMapping("/login/{user-type}")
    // 페이지 넘기는 값(URL의 {user-type} 값)이 enum의 정확한 이름과 동일하면  MemberType으로 바인딩
    public BaseResponse<LoginResponse> login(@PathVariable("user-type") MemberType type,
                                             @RequestBody LoginRequest request) {
        //로그인 서비스 호출
        Member member = memberService.login(type, request);
        //성공 응답 리턴
        return new BaseResponse<>(SUCCESS, new LoginResponse(member.getMemberName()));
    }
}
