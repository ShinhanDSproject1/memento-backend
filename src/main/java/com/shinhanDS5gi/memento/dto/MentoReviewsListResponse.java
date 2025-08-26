package com.shinhanDS5gi.memento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
//멘토 리뷰 조회DTO
public class MentoReviewsListResponse {
    private Long reviewId;
    private String mentosTitle;
    private Integer reviewRating;
    private String mentiName;
    private String reviewContent;
    private String createdAt;
}
