package com.shinhanDS5gi.memento.repository.chat;

import com.shinhanDS5gi.memento.domain.chat.ChattingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChattingParticipantRepository extends JpaRepository<ChattingParticipant, Long> {

    /* 특정 채팅방과 특정 사용자를 기준으로 참여 정보를 조회 */
    Optional<ChattingParticipant> findByChattingRoom_ChattingRoomSeqAndMember_MemberSeq(Long chattingRoomSeq, Long memberSeq);

    /* 채팅방 ID와 보낸 사람 ID를 제외하여 상대방을 찾는 메서드 */
    Optional<ChattingParticipant> findByChattingRoom_ChattingRoomSeqAndMember_MemberSeqNot(Long chattingRoomSeq, Long senderSeq);
}