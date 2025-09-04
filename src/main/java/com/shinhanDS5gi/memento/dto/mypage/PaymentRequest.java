package com.shinhanDS5gi.memento.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String orderId;  // reservationSeq (예약PK 기반으로 서버가 생성)
    private long amount; // price
    private String orderName; //mentosTitle
    private String successUrl; // 서버에서 내려줌
    private String failUrl; // 서버에서 내려줌
}
