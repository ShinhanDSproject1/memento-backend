package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.auth.MentiSignupRequest;
import com.shinhanDS5gi.memento.dto.auth.MentoSignupRequest;
import com.shinhanDS5gi.memento.service.AuthService;
import com.shinhanDS5gi.memento.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;

    /* 회원 탈퇴 */
    @PatchMapping("/member/{memberSeq}")
    public BaseResponse<Void> withdraw(@PathVariable Long memberSeq,
                                       HttpServletRequest req,
                                       HttpServletResponse res) {
        boolean secureCookie = req.isSecure();
        memberService.withdraw(memberSeq, req, res, secureCookie);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 회원가입 (멘토) */
    @PostMapping(value = "/signup/mento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> signupMento(
            @RequestPart("requestDto") @Valid MentoSignupRequest requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile certImage,
            @RequestHeader("Idem-Key") String IdemKey
    ) throws IOException {
        memberService.signupMento(requestDto, certImage, IdemKey);
        return new BaseResponse<>(SUCCESS, null);
    }


    /* 회원가입 (멘티) */
    @PostMapping("/signup/menti")
    public BaseResponse<Void> signupMenti(@RequestBody MentiSignupRequest requestDto,
                                          @RequestHeader("Idem-Key") String idemKey) {
        memberService.signupMenti(requestDto, idemKey);
        return new BaseResponse<>(SUCCESS, null);
    }
}
