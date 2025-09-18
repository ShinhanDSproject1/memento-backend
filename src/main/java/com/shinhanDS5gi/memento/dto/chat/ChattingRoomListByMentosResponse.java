package com.shinhanDS5gi.memento.dto.chat;

import com.shinhanDS5gi.memento.common.exception.BaseException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.chat.ChattingParticipant;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/* 멘토의 채팅방 목록을 멘토스별로 그룹화하여 반환하기 위한 응답 DTO */
@Getter
public class ChattingRoomListByMentosResponse {

    private final Long mentosId;
    private final String mentosTitle;
    private final List<ChatRoomInfo> chatRooms;

    public ChattingRoomListByMentosResponse(Long mentosId, String mentosTitle, List<ChattingRoom> rooms, Long mentorId) {
        this.mentosId = mentosId;
        this.mentosTitle = mentosTitle;
        this.chatRooms = rooms.stream()
                .map(room -> new ChatRoomInfo(room, mentorId))
                .collect(Collectors.toList());
    }

    /* 개별 채팅방의 상세 정보를 담는 내부 DTO */
    @Getter
    public static class ChatRoomInfo {
        private final Long chatRoomId;
        private final String mentiName;
        private final String lastMessage;
        private final LocalDateTime lastMessageAt;
        private final boolean hasUnreadMessage;

        public ChatRoomInfo(ChattingRoom room, Long mentorId) {
            this.chatRoomId = room.getChattingRoomSeq();

            ChattingParticipant mentiParticipant = room.getParticipants().stream()
                    .filter(p -> !p.getMember().getMemberSeq().equals(mentorId))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER));

            this.mentiName = mentiParticipant.getMember().getMemberName();
            this.lastMessage = room.getLastMessage();
            this.lastMessageAt = Optional.ofNullable(room.getLastMessageAt()).orElse(LocalDateTime.MIN);

            ChattingParticipant mentorParticipant = room.getParticipants().stream()
                    .filter(p -> p.getMember().getMemberSeq().equals(mentorId))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER));

            this.hasUnreadMessage = Boolean.TRUE.equals(mentorParticipant.isHasUnreadMessage());

        }
    }
}