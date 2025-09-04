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


}

