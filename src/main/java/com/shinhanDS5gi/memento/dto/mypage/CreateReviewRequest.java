package com.shinhanDS5gi.memento.dto.mypage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/* 리뷰 관련 RequestDTO */
public class CreateReviewRequest {

    private Long reservationSeq;

    @NotNull(message = "리뷰 평점 등록은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하이어야 합니다.")
    private Integer reviewRating;

    @NotBlank(message = "리뷰 내용 입력은 필수입니다.")
    private String reviewContent;

}
