package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMentosListResponse {

    private List<MentosDetail> mentos;
    private boolean hasNext;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MentosDetail{
        private Long mentosSeq;
        private boolean approved;
        private String mentosImg;
        private String mentosTitle;
        private int mentosPrice;
        private String region;
    }
}
