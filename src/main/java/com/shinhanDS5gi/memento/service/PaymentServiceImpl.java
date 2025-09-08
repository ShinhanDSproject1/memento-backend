package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import com.shinhanDS5gi.memento.repository.PaymentRepository;
import com.shinhanDS5gi.memento.repository.chat.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final WebClient tossWebClient;

    // 환불하기
    @Override
    @Transactional
    public void refundFull(Long paymentSeq, String reason) {
        Payment payment = paymentRepository.findById(paymentSeq)
                .orElseThrow(() -> new MentosException(PAYMENT_NOT_FOUND));

        // 1) 토스 환불 API 호출
        String respJson = tossWebClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("cancelReason", reason))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(msg ->
                                Mono.error(new MentosException(REFUND_FAILED))
                        )
                )
                .bodyToMono(String.class) // Toss에서 내려준 JSON 전체 받기
                .block();

        log.info("Toss refund response = {}", respJson);

        // 2) DB 업데이트
        payment.markRefunded();  // 결제 REFUND + INACTIVE

        Reservation reservation = payment.getReservation();
        if (reservation != null) {
            reservation.deactivate(); // 예약 INACTIVE

            chattingRoomRepository.findByPayment(payment)
                    .ifPresent(ChattingRoom::deactivate);// 채팅방 INACTIVE
        }
    }
}
