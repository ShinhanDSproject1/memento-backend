package com.shinhanDS5gi.memento.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/* 커서 기반 페이징 응답을 위한 DTO */
@Getter
@Builder
public class MyMentosSliceResponse<T> {

    private final List<T> content; // 게시글 목록
    private final Long nextCursor;
    private final boolean hasNext; // 다음 페이지 존재 여부

}
