package com.shinhanDS5gi.memento.service;


import com.shinhanDS5gi.memento.dto.mypage.PaymentRequest;

public interface PaymentService {

    /* 결제창 띄우기용 값 (바디 없음, 서버 조회) */
    PaymentRequest init(Long reservationSeq);

    /* 토스 성공 리다이렉트 처리 (confirm 호출 + DB 저장) */
    void confirm(String paymentKey, String orderId, long amount);

    /* 실패 리다이렉트 처리 (필요시 로그/통계) */
    void fail(String code, String message, String orderId);


}

