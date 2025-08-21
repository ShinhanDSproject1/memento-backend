package com.shinhanDS5gi.memento.domain;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long reviewSeq;

    @Column(nullable = false)
    private int reviewRating;

    @Column(nullable = false)
    private String reviewContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mentos_seq")
    private Mentos mentos;

}
