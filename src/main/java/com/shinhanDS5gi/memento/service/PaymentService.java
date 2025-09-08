package com.shinhanDS5gi.memento.service;


import com.shinhanDS5gi.memento.dto.mentos.PaymentRequest;
import com.shinhanDS5gi.memento.dto.mentos.ReservationConfirmedRequest;

public interface PaymentService {

    // 결제 전: Redis 홀더 검증
    void verifyReservationHolder(ReservationConfirmedRequest req, Long memberSeq);

    // 2) 결제창 띄우기용 값 생성
    PaymentRequest init(Long memberSeq, ReservationConfirmedRequest req);

    //토스 성공 리다이렉트 처리 (confirm 호출 + DB 저장)
    void confirm(Long memberSeq, String paymentKey, String orderId, long amount, ReservationConfirmedRequest req);

    //결제 실패 리다이렉트 처리
    void fail(String code, String message, String orderId);

}

