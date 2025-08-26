package com.shinhanDS5gi.memento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
//멘토 리뷰 조회 페이징(CURSOR) DTO
public class MentoReviewsSliceResponse<T> {
    private final List<T> content;
    private final boolean hasNext;
    private final Long nextCursor;
}



