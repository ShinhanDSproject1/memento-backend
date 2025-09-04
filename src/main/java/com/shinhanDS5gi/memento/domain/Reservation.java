package com.shinhanDS5gi.memento.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long reservationSeq;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @Column(name = "mentos_at", nullable = false)
    private LocalDate mentosAt;

    @Column(name="mentos_time")
    private LocalTime mentosTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mentos_seq")
    private Mentos mentos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "reservation")
    private List<Payment> paymentList = new ArrayList<>();
}
