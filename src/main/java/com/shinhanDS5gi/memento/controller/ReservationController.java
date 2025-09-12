package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.reservation.CreateReservationRequest;
import com.shinhanDS5gi.memento.dto.reservation.GetAvailableDateResponse;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /* 멘토의 예약 가능 시간 조회 */
    @GetMapping("/availability/{mentosSeq}")
    public BaseResponse<GetAvailableDateResponse> getAvailableTime(@PathVariable("mentosSeq") Long mentosSeq,
                                                                   @RequestParam("selectedDate") String selectedDate){
        log.info("[ReservationController.getAvailableTime]");
        return new BaseResponse<>(reservationService.getAvailableTime(mentosSeq, selectedDate));
    }

    @PostMapping("/")
    public BaseResponse<Void> makeReservation(@CurrentUser Member member,
                                              @RequestBody CreateReservationRequest createReservationRequest){
        Long currentMemberSeq = member.getMemberSeq();
        log.info("[ReservationController.makeReservation]");
        reservationService.makeReservation(currentMemberSeq, createReservationRequest);
        return new BaseResponse<>(SUCCESS, null);
    }
}
