package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.member.Member;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/* 멘토 기준 나의 멘티 목록 조회 응답 DTO */
@Getter
public class MyMentiResponse {

    private final Long mentosSeq;
    private final String mentosTitle;
    private final List<MentiInfo> mentiList; // 멘티 목록

    public MyMentiResponse(Mentos mentos, List<Reservation> reservations) {
        this.mentosSeq = mentos.getMentosSeq();
        this.mentosTitle = mentos.getMentosTitle();
        this.mentiList = reservations.stream()
                .map(MentiInfo::new)
                .collect(Collectors.toList());
    }

    /* 멘티 각각의 정보를 담는 내부 DTO 클래스 */
    @Getter
    public static class MentiInfo {
        private final Long memberSeq;
        private final String memberName;
        private final LocalDateTime mentosAt; // 멘토링 진행 일시. 멘토링 진행 완료 시 멘티 목록에서 제거해야 하기 때문

        public MentiInfo(Reservation reservation) {
            Member menti = reservation.getMember();
            this.memberSeq = menti.getMemberSeq();
            this.memberName = menti.getMemberName();
            this.mentosAt = reservation.getMentosAt();
        }
    }
}
