package com.shinhanDS5gi.memento.dto;

import lombok.Builder;
import lombok.Getter;

/* 나의 멘토스 목록 조회를 위한 응답 DTO */
@Getter
@Builder
public class MyMentosResponse {

    private final Long mentosSeq;
    private final String mentosTitle;
    private final String mentosImage;
    private final int price;
    private final String region;

}
