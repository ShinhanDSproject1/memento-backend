package com.shinhanDS5gi.memento.controller;


import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.payment.PaymentRequest;
import com.shinhanDS5gi.memento.dto.mentos.ReservationConfirmedRequest;
import com.shinhanDS5gi.memento.dto.payment.PaymentResponse;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 전 예약 확인 및 결제창 실행
     */
    @PostMapping(value = "/mentos/payments/{memberSeq}/init", consumes = "application/json")
    public BaseResponse<PaymentRequest> initAndVerify(
            @CurrentUser Member member,
            @RequestBody ReservationConfirmedRequest req
    ) {
        Long currentMemberSeq = member.getMemberSeq();
        // 1. Redis 홀더 검증
        paymentService.verifyReservationHolder(req, currentMemberSeq);
        // 2. 결제창 띄우기용 값 생성 (successUrl에 memberSeq 치환)
        PaymentRequest res = paymentService.init(currentMemberSeq, req);

        log.info("init OK: memberSeq={}, orderId={}", currentMemberSeq, res.getOrderId());
        return new BaseResponse<>(res);
    }

    /**
     * 결제 성공 콜백
     */
    @PostMapping("/payments/success")
    public BaseResponse<PaymentResponse> success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam long amount
    ) {
        PaymentResponse result = paymentService.confirm(paymentKey, orderId, amount);
        return new BaseResponse<>(SUCCESS, result);
    }

    /**
     * 결제 실패 콜백
     */
    @GetMapping("/payments/fail")
    public BaseResponse<Void> fail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam(required = false) String orderId
    ) {
        paymentService.fail(code, message, orderId);
        return new BaseResponse<>(SUCCESS, null);

    }

    /**
     * 환불하기
     */
    @PostMapping("/mentos/refund/{paymentSeq}")
    public BaseResponse<Void> refund(
                    @CurrentUser Member member,
                    @PathVariable Long paymentSeq) {
        Long currentMemberSeq = member.getMemberSeq();
        paymentService.refundFull(currentMemberSeq, paymentSeq, "USER_REQUEST");
        return new BaseResponse<>(SUCCESS, null);

   }

}
