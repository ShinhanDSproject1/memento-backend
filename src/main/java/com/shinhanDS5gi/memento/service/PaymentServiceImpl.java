package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
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
    private final MentosRepository mentosRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("예약 없음: " + reservationSeq));

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
        // 0) orderId에서 reservationSeq 복구 및 예약 조회
        Long reservationSeq = extractReservationSeqFromOrderId(orderId);
        Reservation reservation = reservationRepository.findById(reservationSeq)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다. reservationSeq=" + reservationSeq));

        Mentos mentos = reservation.getMentos();

        // 1) 금액 검증 (클라이언트 값 신뢰 금지)
        if (mentos.getPrice() != amount) {
            // 금액 불일치 → 결제 실패(6000)
            throw new MentosException(PAYMENT_FAILED);
        }

        // 3) 토스 confirm 호출
        try {
            // (A) Basic Auth 헤더 구성
            String basic = "Basic " + Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            // (B) 승인 요청
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
                    // 토스가 에러 주면 본문 로깅 후 6000 변환
                    .onStatus(HttpStatusCode::isError, res ->
                            res.bodyToMono(String.class).flatMap(body -> {
                                log.error("Toss confirm error: status={}, body={}", res.statusCode(), body);
                                return Mono.error(new MentosException(PAYMENT_FAILED));
                            })
                    )
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            // 네트워크/4xx/5xx 등 예외 케이스 공통 변환
            log.error("Toss confirm exception: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MentosException(PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("Toss confirm unexpected error", e);
            throw new MentosException(PAYMENT_FAILED);
        }

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
        paymentRepository.save(payment);
    }

    /** 실패 콜백: 동일 규격으로 6000 에러 처리 */
    @Override
    @Transactional
    public void fail(String code, String message, String orderId) {
        // 실패 로그만 남기고, 프런트에는 통일된 에러 응답(PAYMENT_FAILED) 전달
        log.warn("Toss payment failed: code={}, message={}, orderId={}", code, message, orderId);
        throw new MentosException(BaseExceptionResponseStatus.PAYMENT_FAILED);

    }

    /** orderId 생성: 6자 이상, 허용문자('_' 사용), 유니크(타임스탬프 포함) */
    private String makeOrderId(Long reservationSeq) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "RES_" + reservationSeq + "_" + ts; // 예) RES_1_20250904151322
    }

    /** "RES_<seq>_<ts>" or "RES-<seq>" or "<seq>" 지원 */
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
