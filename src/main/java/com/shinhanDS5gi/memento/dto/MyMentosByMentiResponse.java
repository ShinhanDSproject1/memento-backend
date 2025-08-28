package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import lombok.Builder;
import lombok.Getter;

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
        String status = reservation.getMentosAt().isBefore(LocalDateTime.now()) ? "진행 완료" : "진행 전";

        return MyMentosByMentiResponse.builder()
                .mentosSeq(mentos.getMentosSeq())
                .mentosTitle(mentos.getMentosTitle())
                .mentosImage(mentos.getMentosImage())
                .price(mentos.getPrice())
                .region(mentos.getMentosBname())
                .progressStatus(status)
                .build();
    }
}