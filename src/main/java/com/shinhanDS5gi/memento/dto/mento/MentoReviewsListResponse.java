package com.shinhanDS5gi.memento.dto.mento;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
//멘토 리뷰 조회DTO
public class MentoReviewsListResponse {
    private Long reviewId;
    private String mentosTitle;
    private Integer reviewRating;
    private String mentiName;
    private String reviewContent;
    private String createdAt;
}
