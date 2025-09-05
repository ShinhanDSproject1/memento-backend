package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.payment.Payment;

public interface PaymentService {

    /* 결제 완료 처리 후 채팅 방 생성 */
    Payment processPaymentCompletion(Long paymentId);
}
