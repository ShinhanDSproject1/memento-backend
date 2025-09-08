package com.shinhanDS5gi.memento.dto.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 서버가 클라이언트로 메시지를 보낼 때 사용하는 DTO
@Getter
@Builder
public class ChattingMessageResponse {

    private Long chattingRoomSeq;
    private Long senderSeq;
    private String senderName;          // 보내는 사람
    private String senderProfileImage;  // 프로필 사진
    private String message;
    private LocalDateTime sentAt;       // 보낸 시간
}
