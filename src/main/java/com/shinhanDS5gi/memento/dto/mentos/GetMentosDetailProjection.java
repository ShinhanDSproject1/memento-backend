package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMentosDetailProjection {
    /**
     * 멘토스 상세조회에서 리뷰를 제외한 애들을 읽어오기 위한 임시 dto
     */
    private final String mentosImage;
    private final String mentosTitle;
    private final String mentosLocation;
    private final int reviewTotalCnt;
    private final double reviewRatingAvg;
    private final String mentoName;
    private final String mentoImg;
    private final String mentoDescription;
    private final String mentosDescription;
    private final int mentosPrice;
}
