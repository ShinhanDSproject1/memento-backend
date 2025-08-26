package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.CreateReviewRequest;
import com.shinhanDS5gi.memento.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final ReviewService reviewService;

    /* 리뷰 작성하기 */
    @PostMapping("/reviews")
    public BaseResponse<Void> createReview(@RequestBody CreateReviewRequest requestDto) {
        Long currentMemberId = 1L;
        reviewService.createReview(currentMemberId, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }
}
