package com.shinhanDS5gi.memento.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateReservationRequest {

    // 예약하기
    private Long mentosSeq;
    private String mentosAt;
    private String mentosTime;
}
