package com.shinhanDS5gi.memento.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.*;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import com.shinhanDS5gi.memento.domain.report.Report;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder //엔티티에 저장하기 위해 넣었음
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long memberSeq;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String memberPwd;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String memberPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType memberType;

    @Column(nullable = false)
    private LocalDate memberBirthDate;

    @Builder.Default //DB에서 NOT NULL로 적혀있기에 null로 들어가지 말라고 작성
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status= BaseStatus.ACTIVE; //Default는 “필드 + 초기값” 세트

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private MentoProfile mentoProfile;

    @Builder.Default //기본생성자여서 엔티티 생성 시에는 null 안 되는데, 빌더로 만들 때는 무시되고 null 들어가는걸 방지
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<MentoCertification> mentoCertificationList  = new ArrayList<>(); 

    @Builder.Default 
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Payment> paymentList = new ArrayList<>();

    @Builder.Default 
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Reservation> reservationList = new ArrayList<>();

    @Builder.Default 
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Report> reportList = new ArrayList<>();


    @Builder.Default 
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Mentos> mentosList = new ArrayList<>();


    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Review> reviewList = new ArrayList<>();

}
