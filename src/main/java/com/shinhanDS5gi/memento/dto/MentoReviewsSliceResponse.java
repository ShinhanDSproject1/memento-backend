package com.shinhanDS5gi.memento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
//멘토 리뷰 조회 페이징(CURSOR) DTO
public class MentoReviewsSliceResponse<T> {
    private final List<T> content; // 신고 목록
    private final boolean hasNext; // 다음 페이지 존재 여부
    private final Long nextCursor; // 다음 페이지 시작
}



