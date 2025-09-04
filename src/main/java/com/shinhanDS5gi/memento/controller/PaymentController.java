package com.shinhanDS5gi.memento.controller;


import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.mypage.PaymentRequest;
import com.shinhanDS5gi.memento.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** 결제 시작 */
    @PostMapping("/mentos/payments/{reservationSeq}")
    public BaseResponse<PaymentRequest> init(@PathVariable Long reservationSeq) {
        return new BaseResponse<>(paymentService.init(reservationSeq));
    }

    /** 결제 성공 콜백  */
    @GetMapping("/payments/success")
    public BaseResponse<Void> success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam long amount
    ) {
        paymentService.confirm(paymentKey, orderId, amount);
        return new BaseResponse<>(SUCCESS, null);
    }

    /** 결제 실패 콜백 */
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
