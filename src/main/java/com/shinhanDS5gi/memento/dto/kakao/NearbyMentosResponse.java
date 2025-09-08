package com.shinhanDS5gi.memento.dto.kakao;

import com.shinhanDS5gi.memento.domain.Mentos;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NearbyMentosResponse {

    private final Long mentoProfileSeq;
    private final String mentoName;
    private final String mentoProfileContent;
    private final String mentoProfileImage;
    private final double latitude;
    private final double longitude;
    private final double distance; // 현재 위치로부터의 거리 (km 단위)
    private final List<MentosInfo> mentosList; // 지도에 보이는 멘토가 진행하는 멘토링의 목록

    @Getter
    @Builder
    public static class MentosInfo {
        private final Long mentosSeq;
        private final String mentosTitle;
        private final int price;

        public static MentosInfo from(Mentos mentos) {
            return MentosInfo.builder()
                    .mentosSeq(mentos.getMentosSeq())
                    .mentosTitle(mentos.getMentosTitle())
                    .price(mentos.getPrice())
                    .build();
        }
    }
}