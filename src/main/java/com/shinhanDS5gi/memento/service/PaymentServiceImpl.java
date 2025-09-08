package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import com.shinhanDS5gi.memento.repository.PaymentRepository;
import com.shinhanDS5gi.memento.repository.chat.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    @Override
    @Transactional
    public Payment processPaymentCompletion(Long paymentId) {
        // 결제 완료 로직 필요

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 결제 상태 변경 로직 필요

        // 결제 완료 후 (성공 시) 채팅방 신규 생성
        ChattingRoom newChatRoom = ChattingRoom.create(payment);

        // 생성된 채팅방과 채팅 참여자 정보 DB에 저장
        chattingRoomRepository.save(newChatRoom);

        return payment;
    }
}
