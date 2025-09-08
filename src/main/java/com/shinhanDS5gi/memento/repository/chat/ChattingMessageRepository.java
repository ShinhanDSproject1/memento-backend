package com.shinhanDS5gi.memento.repository.chat;

import com.shinhanDS5gi.memento.domain.chat.ChattingMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChattingMessageRepository extends MongoRepository<ChattingMessageDocument, String> {
    /* 채팅방 ID를 기준으로 모든 메시지를 시간 순으로 조회 */
    List<ChattingMessageDocument> findByChattingRoomSeqOrderByCreatedAtAsc(Long chattingRoomSeq);
}
