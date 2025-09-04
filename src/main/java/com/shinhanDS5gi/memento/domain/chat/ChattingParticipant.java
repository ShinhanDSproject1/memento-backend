package com.shinhanDS5gi.memento.domain.chat;

import com.shinhanDS5gi.memento.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // updatedAt 자동 업데이트를 위해 추가
@Table(name = "chatting_participant")
public class ChattingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_participant_seq")
    private Long chattingParticipantSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_seq", nullable = false)
    private ChattingRoom chattingRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq", nullable = false)
    private Member member;

    @Column(nullable = false)
    private boolean hasUnreadMessage;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 생성자
    public ChattingParticipant(ChattingRoom room, Member member) {
        this.chattingRoom = room;
        this.member = member;
        this.hasUnreadMessage = false; // 처음에는 안 읽은 메시지 없음이 default
    }

    // 비즈니스 로직
    public void markAsRead() {
        this.hasUnreadMessage = false;
    }

    public void markAsUnread() {
        this.hasUnreadMessage = true;
    }

    // 연관관계 편의 메서드 (내부 사용)
    protected void setChattingRoom(ChattingRoom room) {
        this.chattingRoom = room;
    }
}