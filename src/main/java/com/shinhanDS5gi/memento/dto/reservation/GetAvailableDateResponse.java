package com.shinhanDS5gi.memento.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetAvailableDateResponse {
    private Long mentosSeq;
    private String mentosTitle;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> availableTime;
    private int price;
}
