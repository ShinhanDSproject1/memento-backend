package com.shinhanDS5gi.memento.dto.admin;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/* 모든 신고 내역 조회하기에서 커서 기반 페이징 응답을 위한 DTO */
@Getter
@Builder
public class SelectReportSliceResponse<T> {

    private final List<T> content; // 신고 내역 목록
    private final Long nextCursor;
    private final boolean hasNext; // 다음 페이지 존재 여부
}
