package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.mypage.*;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.MemberService;
import com.shinhanDS5gi.memento.service.MyPageService;
import com.shinhanDS5gi.memento.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final ReviewService reviewService;
    private final MyPageService myPageService;
    private final MemberService memberService;

    /* 리뷰 작성하기 */
    @PostMapping("/reviews")
    public BaseResponse<Void> createReview(@CurrentUser Member member, @RequestBody CreateReviewRequest requestDto, @RequestHeader("Idem-Key") String idemKey) {
        log.info("[MypageController.createReview]==> memberSeq : "+ String.valueOf(member.getMemberSeq()));
        Long currentMemberSeq = member.getMemberSeq();
        reviewService.createReview(currentMemberSeq, requestDto, idemKey);
        return new BaseResponse<>(null);
    }

    /* 나의 프로필 조회하기 */
    @GetMapping("/profile")
    public BaseResponse<MyProfileResponse> getMyProfile(@CurrentUser Member member) {
        Long currentMemberSeq = member.getMemberSeq();
        MyProfileResponse profile = myPageService.getMyProfile(currentMemberSeq);
        return new BaseResponse<>(SUCCESS, profile);
    }

    /* 나의 프로필 정보(휴대폰 번호, 생년월일) 수정하기 */
    @PatchMapping("/profile")
    public BaseResponse<Void> updateMyProfile(@CurrentUser Member member, @RequestBody UpdateMyProfileRequest requestDto) {
        Long currentMemberSeq = member.getMemberSeq();
        myPageService.updateMyProfile(currentMemberSeq, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 나의 비밀번호 변경하기 */
    @PatchMapping("/password")
    public BaseResponse<Void> updateMyPassword(@CurrentUser Member member, @RequestBody UpdateMyPasswordRequest requestDto) {
        Long currentMemberSeq = member.getMemberSeq();
        myPageService.updateMyPassword(currentMemberSeq, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 나의 멘토스 내역 조회하기 (멘티 기준) */
    @GetMapping("/my-mentos-list")
    public BaseResponse<MyMentosByMentiSliceResponse> getMyMentoringHistory(
            @CurrentUser Member member,
            @RequestParam(defaultValue = "3") int limit, // 3개씩 보여주기
            @RequestParam(required = false) Long cursor
    ) {
        Long currentMemberSeq = member.getMemberSeq();
        var page = myPageService.getMyMentosByMenti(currentMemberSeq, limit, cursor);
        return new BaseResponse<>(SUCCESS, page);
    }

    /* 회원 탈퇴 */
    @PatchMapping("/withdraw")
    public BaseResponse<Void> withdraw(@CurrentUser Member member,
                                       HttpServletRequest req,
                                       HttpServletResponse res) {
        Long currentMemberSeq = member.getMemberSeq();
        boolean secureCookie = req.isSecure();
        memberService.withdraw(currentMemberSeq, req, res, secureCookie);
        return new BaseResponse<>(SUCCESS, null);
    }
}
