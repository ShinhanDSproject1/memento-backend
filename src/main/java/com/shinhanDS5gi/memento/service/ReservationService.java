package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.reservation.CreateReservationRequest;
import com.shinhanDS5gi.memento.dto.reservation.GetAvailableDateResponse;

public interface ReservationService {

    /* 선택한 날짜에 예약이 가능한 시간대 보여주기 */
    GetAvailableDateResponse getAvailableTime(Long mentosSeq, Long memberSeq, String selectedDate);

    /* 에약하기 */
    void makeReservation(Long memberSeq, CreateReservationRequest createReservationRequest);
}
