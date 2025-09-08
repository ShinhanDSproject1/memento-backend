package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.payment.Payment;

public interface PaymentService {

    /* 환불하기 */
    void refundFull(Long paymentSeq, String reason);
}
