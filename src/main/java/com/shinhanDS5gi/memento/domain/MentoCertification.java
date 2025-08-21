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
public class MentoCertification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long mentoCertificationSeq;

    @Column(nullable = false)
    private String mentoCertificationName;

    @Column(nullable = false)
    private String mentoCertificationImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;
}
