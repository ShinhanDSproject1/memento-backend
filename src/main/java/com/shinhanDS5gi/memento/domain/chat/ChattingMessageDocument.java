package com.shinhanDS5gi.memento.domain.chat;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@Document(collection = "chat_messages") // MongoDB의 chat_messages 컬렉션에 매핑
public class ChattingMessageDocument {

    @Id
    private String id; // MongoDB의 고유 ID

    private Long chattingRoomSeq;
    private Long senderSeq;
    private String senderName;
    private String senderProfileImage;
    private String message;
    private LocalDateTime createdAt;
}
