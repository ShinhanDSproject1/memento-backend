package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.*;
import com.shinhanDS5gi.memento.security.JwtTokenUtil;
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
import java.time.Duration;
import java.util.Map;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final AuthService  authService;
    private final JwtTokenUtil jwtTokenUtil;

    //at 재발급을 위해 RT 보내는 엔드포인트
    @PostMapping("/reissue")
    public BaseResponse<AccessTokenResponse> reissue(HttpServletRequest req, HttpServletResponse res) {
        boolean secureCookie = req.isSecure()
                || "https".equalsIgnoreCase(req.getHeader("X-Forwarded-Proto"));

        AccessTokenResponse response = authService.refresh(req, res, secureCookie);
        return new BaseResponse<>(SUCCESS, response);
    }

    /* 로그아웃 */
    //AT 블랙리스트 등록 + RT 삭제 + 쿠키 제거
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        boolean secureCookie = req.isSecure();
        authService.logout(req, res, secureCookie);
        return new BaseResponse<>(SUCCESS, null);
    }


    /* 로그인 */
    //비번검증 → AT/RT 발급 → Redis RT 저장 → Set-Cookie(RT),Body(AT) 처리
    @PostMapping("/login/{user-type}")
    public BaseResponse<LoginResponse> login(
            @PathVariable("user-type") MemberType type,
            @RequestBody LoginRequest request,
            HttpServletRequest req,
            HttpServletResponse res) {
        Map<String, Object> result = authService.issueTokens(type, request);
        Member m = (Member) result.get("member");
        String at = (String) result.get("accessToken");
        String rt = (String) result.get("refreshToken");
        jwtTokenUtil.setCookie(res, "RT", rt, Duration.ofDays(14), req.isSecure());
        return new BaseResponse<>(SUCCESS,
                new LoginResponse(m.getMemberName(), m.getMemberType().name(), at));
    }

    /* 회원가입 (멘토) */
    @PostMapping(value = "/signup/mento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> signupMento(
            @RequestPart("requestDto") @Valid MentoSignupRequest requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile certImage,
            @RequestHeader("Idem-Key") String IdemKey
    ) throws IOException {

        boolean hasName = requestDto.getCertificationName() != null
                && !requestDto.getCertificationName().isBlank();
        boolean hasFile = (certImage != null && !certImage.isEmpty());
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
