package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.chat.*;

import java.util.List;

public interface ChattingService {

    /* 멘토의 채팅방 목록을 멘토스 그룹 별로 조회 */
    List<ChattingRoomListByMentosResponse> getChatRoomsByMentosForMentor(Long mentorId);

    /* 멘티의 채팅방 목록을 멘토스 그룹 별로 조회 */
    List<MentiChatRoomListByMentosResponse> getChatRoomsForMenti(Long mentiId);

    /* 클라이언트로부터 받은 채팅 메시지를 처리하고 저장 */
    ChattingMessageResponse processAndSaveMessage(ChattingMessageRequest messageDto, Member sender);

    /* 특정 채팅방의 상세 정보와 이전 대화 메시지 내역 조회 */
    ChattingRoomDetailResponse getChattingRoomDetails(Long chattingRoomSeq, Long currentMemberSeq);

    /* 특정 채팅방의 메시지를 모두 읽음 처리 */
    void markAsRead(Long chattingRoomSeq, Long memberSeq);
}