package com.shinhanDS5gi.memento.dto.mentos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationConfirmedRequest {
    private long mentosSeq;
    private String mentosAt;
    private String mentosTime;
}
