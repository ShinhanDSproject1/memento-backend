package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.Mentos;
import lombok.Getter;

/* 나의 멘토스 목록 조회를 위한 응답 DTO */
@Getter
public class MyMentosResponse {

    private final Long mentosSeq;
    private final String mentosTitle;
    private final String mentosImage;
    private final int price;
    private final String region;

    public MyMentosResponse(Mentos mentos) {
        this.mentosSeq = mentos.getMentosSeq();
        this.mentosTitle = mentos.getMentosTitle();
        this.mentosImage = mentos.getMentosImage();
        this.price = mentos.getPrice();
        this.region = mentos.getMentosBname();
    }
}
