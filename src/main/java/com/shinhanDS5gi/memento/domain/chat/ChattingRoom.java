package com.shinhanDS5gi.memento.domain.chat;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 보호
@Table(name = "chatting_room")
public class ChattingRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_seq")
    private Long chattingRoomSeq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_seq", unique = true, nullable = false)
    private Payment payment;

    @Column(length = 500)
    private String lastMessage;

    private LocalDateTime lastMessageAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChattingParticipant> participants = new ArrayList<>();

    // 생성 메서드
    public static ChattingRoom create(Payment payment) {
        ChattingRoom room = new ChattingRoom();
        room.payment = payment;
        room.status = BaseStatus.ACTIVE;
        // 멘토와 멘티를 참여자로 추가
        room.addParticipant(new ChattingParticipant(room, payment.getMember())); // 멘티
        room.addParticipant(new ChattingParticipant(room, payment.getReservation().getMentos().getMember())); // 멘토
        return room;
    }

    // 연관관계 편의 메서드
    private void addParticipant(ChattingParticipant participant) {
        this.participants.add(participant);
        participant.setChattingRoom(this); // 양방향 연관관계 설정
    }

    // 비즈니스 로직
    public void updateLastMessage(String message, LocalDateTime sentAt) {
        this.lastMessage = message;
        this.lastMessageAt = sentAt;
    }

    //환불시 inactive
    public void deactivate() {
        this.status = BaseStatus.INACTIVE;
    }
}