package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.Mentos;
import lombok.Getter;

/* 멘토스 게시글 상세 정보 응답 DTO */
@Getter
public class MentosDetailResponse {

    private final Long mentosSeq;
    private final String mentosTitle;
    private final String mentosContent;
    private final int price;
    private final String mentosImage;
    private final String mentosPostcode;
    private final String mentosRoadaddress;
    private final String mentosBname;
    private final String mentosDetail;
    private final String keywordOne;
    private final String keywordTwo;
    private final String keywordThree;

    public MentosDetailResponse(Mentos mentos) {
        this.mentosSeq = mentos.getMentosSeq();
        this.mentosTitle = mentos.getMentosTitle();
        this.mentosContent = mentos.getMentosContent();
        this.price = mentos.getPrice();
        this.mentosImage = mentos.getMentosImage();
        this.mentosPostcode = mentos.getMentosPostcode();
        this.mentosRoadaddress = mentos.getMentosRoadaddress();
        this.mentosBname = mentos.getMentosBname();
        this.mentosDetail = mentos.getMentosDetail();
        this.keywordOne = mentos.getKeywordOne();
        this.keywordTwo = mentos.getKeywordTwo();
        this.keywordThree = mentos.getKeywordThree();
    }
}