package com.shinhanDS5gi.memento.dto.mypage;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MyMentosByMentiResponse {

    private final Long mentosSeq;
    private final String mentosTitle;
    private final String mentosImage;
    private final int price;
    private final String region;
    private final String progressStatus; /* 진행 전 | 진행 완료 */

    public static MyMentosByMentiResponse from(Reservation reservation) {
        Mentos mentos = reservation.getMentos();
        String status = reservation.getMentosAt().isBefore(LocalDate.now()) ? "진행 완료" : "진행 전";

        String region = null;
        if (mentos.getMember() != null && mentos.getMember().getMentoProfile() != null) {
            region = mentos.getMember().getMentoProfile().getMentoBname();
        }

        return MyMentosByMentiResponse.builder()
                .mentosSeq(mentos.getMentosSeq())
                .mentosTitle(mentos.getMentosTitle())
                .mentosImage(mentos.getMentosImage())
                .price(mentos.getPrice())
                .region(region)
                .progressStatus(status)
                .build();
    }
}