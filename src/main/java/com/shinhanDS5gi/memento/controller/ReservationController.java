package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.reservation.CreateReservationRequest;
import com.shinhanDS5gi.memento.dto.reservation.GetAvailableDateResponse;
import com.shinhanDS5gi.memento.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/availability/{mentosSeq}/{memberSeq}")
    public BaseResponse<GetAvailableDateResponse> getAvailableTime(@PathVariable("mentosSeq") Long mentosSeq,
                                                                   @PathVariable("memberSeq") Long memberSeq,
                                                                   @RequestParam("selectedDate") String selectedDate){
        log.info("[ReservationController.getAvailableTime]");
        return new BaseResponse<>(reservationService.getAvailableTime(mentosSeq, memberSeq, selectedDate));
    }

    @PostMapping("/{memberSeq}")
    public BaseResponse<Void> makeReservation(@PathVariable("memberSeq") Long memberSeq,
                                              @RequestBody CreateReservationRequest createReservationRequest){
        log.info("[ReservationController.makeReservation]");
        reservationService.makeReservation(memberSeq, createReservationRequest);
        return new BaseResponse<>(null);
    }
}
