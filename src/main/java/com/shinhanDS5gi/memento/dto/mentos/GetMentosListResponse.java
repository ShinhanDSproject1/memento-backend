package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetMentosListResponse {

    private List<MentosDetail> mentos;
    private boolean hasNext;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MentosDetail{
        private Long mentosSeq;
        private boolean isApproved;
        private String mentosImg;
        private String mentosTitle;
        private int mentosPrice;
        private String region;
    }
}
