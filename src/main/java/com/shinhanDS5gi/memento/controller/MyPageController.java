package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.mypage.CreateReviewRequest;
import com.shinhanDS5gi.memento.dto.mypage.MyMentosByMentiSliceResponse;
import com.shinhanDS5gi.memento.dto.mypage.MyProfileResponse;
import com.shinhanDS5gi.memento.dto.mypage.UpdateMyPasswordRequest;
import com.shinhanDS5gi.memento.dto.mypage.UpdateMyProfileRequest;
import com.shinhanDS5gi.memento.service.MyPageService;
import com.shinhanDS5gi.memento.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final ReviewService reviewService;
    private final MyPageService myPageService;

    /* 리뷰 작성하기 */
    @PostMapping("/reviews")
    public BaseResponse<Void> createReview(@RequestBody CreateReviewRequest requestDto) {
        Long currentMemberSeq = 1L;
        reviewService.createReview(currentMemberSeq, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 나의 프로필 조회하기 */
    @GetMapping("/profile")
    public BaseResponse<MyProfileResponse> getMyProfile() {
        Long currentMemberSeq = 1L;
        MyProfileResponse profile = myPageService.getMyProfile(currentMemberSeq);
        return new BaseResponse<>(SUCCESS, profile);
    }

    /* 나의 프로필 정보(휴대폰 번호, 생년월일) 수정하기 */
    @PatchMapping("/profile")
    public BaseResponse<Void> updateMyProfile(@RequestBody UpdateMyProfileRequest requestDto) {
        Long currentMemberSeq = 1L;
        myPageService.updateMyProfile(currentMemberSeq, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 나의 비밀번호 변경하기 */
    @PatchMapping("/password")
    public BaseResponse<Void> updateMyPassword(@RequestBody UpdateMyPasswordRequest requestDto) {
        Long currentMemberSeq = 1L;
        myPageService.updateMyPassword(currentMemberSeq, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 나의 멘토스 내역 조회하기 (멘티 기준) */
    @GetMapping("/my-mentos-list")
    public BaseResponse<MyMentosByMentiSliceResponse> getMyMentoringHistory(
            @RequestParam(defaultValue = "3") int limit, // 3개씩 보여주기
            @RequestParam(required = false) Long cursor
    ) {
        Long currentMemberSeq = 1L;
        var page = myPageService.getMyMentosByMenti(currentMemberSeq, limit, cursor);
        return new BaseResponse<>(SUCCESS, page);
    }
}
