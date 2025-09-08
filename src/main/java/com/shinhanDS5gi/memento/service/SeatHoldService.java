package com.shinhanDS5gi.memento.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SeatHoldService {

    /** 날짜 기준으로 홀드된 슬롯들 조회 (모든 홀더 포함) */
    List<LocalTime> findHeldSlots(long mentosSeq, LocalDate date);

    /** 홀드 시도: 없을 때만 생성(NX) + TTL 설정, 성공이면 true */
    boolean holdSlot(long mentosSeq, LocalDate date, LocalTime time, String holderId);

    /** 홀드 해제(결제 취소 등) */
    void releaseSlot(long mentosSeq, LocalDate date, LocalTime time);

    /** mentosSeq, 날짜, 시간 기준으로 홀드된 슬롯 여부 확인 */
    boolean isHeld(long mentosSeq, LocalDate date, LocalTime time);

    Optional<Long> findMemberSeqByKey(Long mentosSeq, LocalDate mentosAt, LocalTime mentosTime);
}