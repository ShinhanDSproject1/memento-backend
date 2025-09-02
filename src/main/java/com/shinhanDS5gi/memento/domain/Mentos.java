package com.shinhanDS5gi.memento.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mentos extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long mentosSeq;

    @Column(nullable = false)
    private String mentosTitle;

    @Column(nullable = false)
    private String mentosContent;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String mentosImage;

    @Column(nullable = false)
    private String mentosPostcode;

    @Column(nullable = false)
    private String mentosRoadaddress;

    @Column(nullable = false)
    private String mentosBname;

    private String mentosDetail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_seq")
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "mentos")
    private List<Reservation> reservationList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "mentos")
    private List<Report> reportList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "mentos")
    private List<Review> reviewList = new ArrayList<>();

    /* 멘토스 정보 수정*/
    public void update(UpdateMentosRequest requestDto, String newImageUrl) {
        this.mentosTitle = requestDto.getMentosTitle();
        this.mentosContent = requestDto.getMentosContent();
        this.price = requestDto.getPrice();
        this.mentosPostcode = requestDto.getMentosPostcode();
        this.mentosRoadaddress = requestDto.getMentosRoadaddress();
        this.mentosBname = requestDto.getMentosBname();
        this.mentosDetail = requestDto.getMentosDetail();
        if (newImageUrl != null) {
            this.mentosImage = newImageUrl;
        }
    }

    /* 멘토스 상태 비활성화 */
    public void inactivate() {
        this.status = BaseStatus.INACTIVE;
    }

    //멘토스 생성하기
    public Mentos(String mentosTitle, String mentosContent, int price, String mentosImage, String mentosPostcode,
                  String mentosRoadaddress, String mentosBname, String mentosDetail, Category category, Member member, BaseStatus status) {
        this.mentosTitle = mentosTitle;
        this.mentosContent = mentosContent;
        this.price = price;
        this.mentosImage = mentosImage;
        this.mentosPostcode = mentosPostcode;
        this.mentosRoadaddress = mentosRoadaddress;
        this.mentosBname = mentosBname;
        this.mentosDetail = mentosDetail;
        this.category = category;
        this.member = member;
        this.status = status;
    }

}