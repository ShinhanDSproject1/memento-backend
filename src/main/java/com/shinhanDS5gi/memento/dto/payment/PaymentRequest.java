package com.shinhanDS5gi.memento.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
//토스한테 보내는 Request
public class PaymentRequest {
    private String orderId;  // reservationSeq
    private long amount; // price
    private String orderName; //mentosTitle
    private String successUrl; // 서버에서 내려줌
    private String failUrl; // 서버에서 내려줌
}
