package com.shinhanDS5gi.memento.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 클라이언트가 서버로 메시지를 보낼 때 사용하는 DTO
@Getter
@NoArgsConstructor
public class ChattingMessageRequest {
    private Long chattingRoomSeq; // 채팅방
    //private Long senderSeq;       // 보내는 사람
    private String message;
}
