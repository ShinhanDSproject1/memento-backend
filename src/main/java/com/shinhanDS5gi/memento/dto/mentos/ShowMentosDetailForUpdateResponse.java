package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShowMentosDetailForUpdateResponse {
    /**
     * 멘토스 수정할 때 수정하기 이전 값을 불러오기 위한 api dto
     */
    private Long mentosSeq;
    private String mentosTitle;
    private Long categorySeq;
    private String mentosContent;
    private String mentosImage;
    private int price;

}
