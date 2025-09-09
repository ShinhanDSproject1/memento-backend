package com.shinhanDS5gi.memento.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MentoTimeWindowProjection {
    private LocalTime startTime;
    private LocalTime endTime;
    private String availableDays;
}
