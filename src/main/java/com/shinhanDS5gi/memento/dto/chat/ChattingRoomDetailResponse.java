package com.shinhanDS5gi.memento.dto.chat;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

    @Getter
    @Builder
    public class ChattingRoomDetailResponse {

        private Long chattingRoomSeq;
        private String mentosTitle;
        private List<ParticipantInfo> participants;     // 참여자 정보 목록 (이름, 프사)
        private List<ChattingMessageResponse> messages; // 과거 메시지 목록 (대화 내용)

        @Getter
        @Builder
        public static class ParticipantInfo {
            private Long memberSeq;
            private String memberName;
            private String profileImage;
        }
    }