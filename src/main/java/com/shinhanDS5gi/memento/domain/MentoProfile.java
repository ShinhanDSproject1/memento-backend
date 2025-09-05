package com.shinhanDS5gi.memento.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.mento.UpdateMentoProfileRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder //엔티티에 저장하기 위해 넣었음
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

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String mentoPostcode;

    @Column(nullable = false)
    private String mentoRoadAddress;

    @Column
    private String mentoDetail;

    @Column(nullable = false)
    private String mentoBname;

    @Column(nullable = false)
    private String availableDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "member_seq", unique = true)
    private Member member;

    public MentoProfile(String mentoProfileContent, String mentoProfileImage, BaseStatus status, Member member) {
        this.mentoProfileContent = mentoProfileContent;
        this.mentoProfileImage = mentoProfileImage;
        this.status = status;
        this.member = member;
    }

    /* 멘토 프로필 수정하기 */
    public void update(UpdateMentoProfileRequest dto, String newImageUrl) {
        this.mentoProfileContent = dto.getMentoProfileContent();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.availableDays = dto.getAvailableDays();
        this.mentoPostcode = dto.getMentoPostcode();
        this.mentoRoadAddress = dto.getMentoRoadAddress();
        this.mentoBname = dto.getMentoBname();
        this.mentoDetail = dto.getMentoDetail();

        if (newImageUrl != null) {
            this.mentoProfileImage = newImageUrl;
        }
    }
}