package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.repository.chat.ChattingRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.payment.PayType;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import com.shinhanDS5gi.memento.dto.mypage.PaymentRequest;
import com.shinhanDS5gi.memento.repository.PaymentRepository;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;


import com.shinhanDS5gi.memento.common.exception.MentosException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final WebClient tossWebClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${app.url.success}")
    private String successUrl;

    @Value("${app.url.fail}")
    private String failUrl;

    /** 결제창 띄우기용 값: 서버가 금액/이름 조회 후 응답 */
    @Override
    public PaymentRequest init(Long reservationSeq) {

        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationSeq)
                .orElseThrow(() -> new MentosException(RESERVATION_NOT_FOUND));

        Mentos mentos = reservation.getMentos();

        long amount = mentos.getPrice();
        String orderName = mentos.getMentosTitle();
        String orderId = makeOrderId(reservationSeq); // ★ 6자 이상 & 허용문자

        return new PaymentRequest(
                orderId,
                amount,
                orderName,
                successUrl,
                failUrl
        );
    }

    /** 성공 콜백: 토스 confirm -> 검증 -> Payment 저장 */
    @Transactional
    @Override
    public void confirm(String paymentKey, String orderId, long amount) {

        // 1) orderId에서 reservationSeq 복구 및 예약 조회
        Long reservationSeq = extractReservationSeqFromOrderId(orderId);
        Reservation reservation = reservationRepository.findById(reservationSeq)
                .orElseThrow(() -> new MentosException(RESERVATION_NOT_FOUND));

        Mentos mentos = reservation.getMentos();

        // 2) 금액 검증-> 실패하면 결제 실패
        if (mentos.getPrice() != amount) {
            throw new MentosException(PAYMENT_FAILED);
        }

        // 3) 토스 confirm 호출
        try {
            // 시크릿키 인코딩해서 Basic Auth 헤더 만들기
            String basic = "Basic " + Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            // 토스 승인(confirm) API 호출
            tossWebClient.post()
                    .uri("/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, basic)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "paymentKey", paymentKey,
                            "orderId", orderId,
                            "amount", amount
                    ))
                    .retrieve()
                    // 토스가 결제실패 주면 오류 응답 매핑
                    .onStatus(HttpStatusCode::isError, res ->
                            res.bodyToMono(String.class).flatMap(body -> {
                                log.error("Toss confirm error: status={}, body={}", res.statusCode(), body);
                                return Mono.error(new MentosException(PAYMENT_FAILED));
                            })
                    )
                    //정상 응답 → 결제 승인 성공으로 처리
                    .toBodilessEntity()
                    .block();
            //예외처리(네트워크 및, 모든 예외)
        } catch (WebClientResponseException e) {
            log.error("Toss confirm exception: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MentosException(PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Toss confirm unexpected error", e);
            throw new MentosException(PAYMENT_FAILED);
        }

        //예약을 inactive로 변경
        reservation.deactivate();

        // 4) 승인 성공 → 결제 저장
        Payment payment = new Payment(
                null,
                LocalDateTime.now(),             // payedAt
                (int) amount,                    // price
                PayType.PAID,                    // 결제상태
                BaseStatus.ACTIVE,               // 활성
                reservation.getMember(),         // 결제자
                reservation                      // 예약
        );

        // 결제 완료 후 (성공 시) 채팅방 신규 생성
        ChattingRoom newChatRoom = ChattingRoom.create(payment);

        //결제내역 저장
        paymentRepository.save(payment);

        // 생성된 채팅방과 채팅 참여자 정보 DB에 저장
        chattingRoomRepository.save(newChatRoom);


    }

    /** 실패 콜백: 동일 규격으로 6000 에러 처리 */
    @Override
    @Transactional
    public void fail(String code, String message, String orderId) {
        log.warn("Toss payment failed: code={}, message={}, orderId={}", code, message, orderId);
        throw new MentosException(BaseExceptionResponseStatus.PAYMENT_FAILED);

    }

    /** 토스가 원하는 규정에 맞게 orderId 생성(예약번호) */
    private String makeOrderId(Long reservationSeq) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "RES_" + reservationSeq + "_" + ts; // 예) RES_1_20250904151322
    }

    /** orderId를 해석해서 예약번호(reservationSeq)만 복구  */
    private Long extractReservationSeqFromOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId가 비어있습니다.");
        }
        if (orderId.startsWith("RES_")) {
            String[] p = orderId.split("_");
            if (p.length >= 3) return Long.parseLong(p[1]);
            throw new IllegalArgumentException("orderId 포맷 오류: " + orderId);
        }
        if (orderId.startsWith("RES-")) {
            return Long.parseLong(orderId.substring(4));
        }
        return Long.parseLong(orderId);
    }
}
