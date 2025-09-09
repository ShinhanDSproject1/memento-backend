package com.shinhanDS5gi.memento.dto.kakao;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NearbyMentoProfileProjection {

    private final Long mentoProfileSeq;
    private final Double distance;

    public NearbyMentoProfileProjection(Long mentoProfileSeq, Double distance) {
        this.mentoProfileSeq = mentoProfileSeq;
        this.distance = distance;
    }
}