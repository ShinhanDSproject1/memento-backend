package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Review;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
/* 리뷰 관련 RequestDTO */
public class CreateReviewRequest {

    @NotNull(message = "리뷰 대상 멘토스 ID는 필수입니다.")
    private Long mentosSeq;

    @NotNull(message = "리뷰 평점 등록은 필수입니다.")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하이어야 합니다.")
    private Integer reviewRating;

    @NotBlank(message = "리뷰 내용 입력은 필수입니다.")
    private String reviewContent;

    public Review toEntity(Member member, Mentos mentos) {
        return new Review(
                null,
                this.reviewRating,
                this.reviewContent,
                BaseStatus.ACTIVE,
                member,
                mentos
        );
    }
}
