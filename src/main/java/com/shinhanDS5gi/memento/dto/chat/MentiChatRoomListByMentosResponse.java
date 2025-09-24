package com.shinhanDS5gi.memento.dto.chat;

import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.chat.ChattingParticipant;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 멘티의 채팅방 목록을 멘토스별로 그룹화하여 반환하기 위한 응답 DTO
 */
@Getter
public class MentiChatRoomListByMentosResponse {

    private final Long mentosId;
    private final String mentosTitle;
    private final List<ChatRoomInfo> chatRooms;

    public MentiChatRoomListByMentosResponse(Long mentosId, String mentosTitle, List<ChattingRoom> rooms, Long menteeId) {
        this.mentosId = mentosId;
        this.mentosTitle = mentosTitle;
        this.chatRooms = rooms.stream()
                .map(room -> new ChatRoomInfo(room, menteeId))
                .collect(Collectors.toList());
    }

    /**
     * 개별 채팅방의 상세 정보를 담는 내부 DTO (멘티 관점)
     */
    @Getter
    public static class ChatRoomInfo {
        private final Long chatRoomId;
        private final String mentorName; // 상대방인 '멘토'의 이름
        private final String mentorProfileImage; // 상대방인 '멘토'의 프로필 이미지
        private final String lastMessage;
        private final LocalDateTime lastMessageAt;
        private final boolean hasUnreadMessage;

        public ChatRoomInfo(ChattingRoom room, Long menteeId) {
            this.chatRoomId = room.getChattingRoomSeq();

            // 1. 상대방(멘토) 참여자 정보 찾기
            ChattingParticipant mentorParticipant = room.getParticipants().stream()
                    .filter(p -> !p.getMember().getMemberSeq().equals(menteeId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("상대방(멘토) 정보를 찾을 수 없습니다."));

            this.mentorName = mentorParticipant.getMember().getMemberName();
            this.mentorProfileImage = Optional.ofNullable(mentorParticipant.getMember().getMentoProfile())
                    .map(MentoProfile::getMentoProfileImage)
                    .orElse(null);

            // 2. 나(멘티)의 참여자 정보 찾기 (안 읽은 메시지 확인용)
            ChattingParticipant menteeParticipant = room.getParticipants().stream()
                    .filter(p -> p.getMember().getMemberSeq().equals(menteeId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("본인(멘티) 참여 정보를 찾을 수 없습니다."));

            this.hasUnreadMessage = Boolean.TRUE.equals(menteeParticipant.isHasUnreadMessage());
            this.lastMessage = room.getLastMessage();
            this.lastMessageAt = Optional.ofNullable(room.getLastMessageAt()).orElse(LocalDateTime.MIN);
        }
    }
}