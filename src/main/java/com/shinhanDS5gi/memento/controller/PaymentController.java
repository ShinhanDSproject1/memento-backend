package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentos")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/refund/{paymentSeq}")
    public BaseResponse<Void> refund(@PathVariable Long paymentSeq) {
        paymentService.refundFull(paymentSeq, "USER_REQUEST");
        return new BaseResponse<>(SUCCESS, null);
    }
}