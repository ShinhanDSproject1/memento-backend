package com.shinhanDS5gi.memento.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(nullable = false)
    private String keywordOne;
    @Column(nullable = false)
    private String keywordTwo;
    @Column(nullable = false)
    private String keywordThree;

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

}