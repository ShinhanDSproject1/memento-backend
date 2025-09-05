package com.shinhanDS5gi.memento.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MentoProfile extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long mentoProfileSeq;

    @Column(nullable = false)
    private String mentoProfileContent;

    @Column(nullable = false)
    private String mentoProfileImage;

    private LocalTime startTime;
    private LocalTime endTime;
    private String mentoPostcode;
    private String mentoRoadaddress;
    private String mentoDetail;
    private String mentoBname;
    private String availableDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "member_seq", unique = true)
    private Member member;

    /* 멘토 프로필 수정하기 */
    public void update(String content, String imageUrl) {
        this.mentoProfileContent = content;

        if (imageUrl != null) {
            this.mentoProfileImage = imageUrl;
        }
    }
}
