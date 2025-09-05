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
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * (테스트용 임시 API)
     * 특정 결제를 완료 처리하고 채팅방을 생성합니다.
     * @param paymentId 결제 ID
     */
    @PostMapping("/{paymentId}/complete")
    public BaseResponse<Void> completePaymentAndCreateChatRoom(@PathVariable Long paymentId) {
        paymentService.processPaymentCompletion(paymentId);
        return new BaseResponse<>(SUCCESS, null);
    }
}