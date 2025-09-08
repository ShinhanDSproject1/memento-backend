package com.shinhanDS5gi.memento.controller;


import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.mentos.PaymentRequest;
import com.shinhanDS5gi.memento.dto.mentos.ReservationConfirmedRequest;
import com.shinhanDS5gi.memento.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 전 예약 확인 및 결제창 실행
     */
    @PostMapping(value = "/mentos/payments/{memberSeq}/init", consumes = "application/json")
    public BaseResponse<PaymentRequest> initAndVerify(
            @PathVariable Long memberSeq,
            @RequestBody ReservationConfirmedRequest req
    ) {
        // 1. Redis 홀더 검증
        paymentService.verifyReservationHolder(req, memberSeq);
        // 2. 결제창 띄우기용 값 생성 (successUrl에 memberSeq 치환)
        PaymentRequest res = paymentService.init(memberSeq, req);

        log.info("init OK: memberSeq={}, orderId={}", memberSeq, res.getOrderId());
        return new BaseResponse<>(res);
    }

    /**
     * 결제 성공 콜백
     */
    @PostMapping("/payments/success")
    public BaseResponse<Void> success(
            @PathVariable Long memberSeq,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam long amount,
            @RequestBody ReservationConfirmedRequest req
    ) {
        paymentService.confirm(memberSeq, paymentKey, orderId, amount, req);
        return new BaseResponse<>(SUCCESS, null);
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
}
