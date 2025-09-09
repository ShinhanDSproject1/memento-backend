package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.QMentoProfile;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.payment.PayType;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import com.shinhanDS5gi.memento.dto.payment.PaymentRequest;
import com.shinhanDS5gi.memento.dto.mentos.ReservationConfirmedRequest;
import com.shinhanDS5gi.memento.dto.payment.PaymentResponse;
import com.shinhanDS5gi.memento.repository.PaymentRepository;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.chat.ChattingRoomRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.shinhanDS5gi.memento.common.exception.MentosException;

@Slf4j

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final SeatHoldService seatHoldService;
    private final MentosRepository mentosRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final WebClient tossWebClient;
    private final MemberRepository memberRepository;


    @Value("${app.url.success}")
    private String successUrl;

    @Value("${app.url.fail}")
    private String failUrl;


    /**
     * 1) 결제 전: Redis 홀더 검증
     */
    @Override
    public void verifyReservationHolder(ReservationConfirmedRequest req, Long memberSeq) {
        LocalDate date = LocalDate.parse(req.getMentosAt());
        LocalTime time = LocalTime.parse(req.getMentosTime());

        Long holder = seatHoldService.findMemberSeqByKey(req.getMentosSeq(), date, time)
                .orElseThrow(() -> new MentosException(PAYMENT_FAILED));
        if (!holder.equals(memberSeq)) throw new MentosException(PAYMENT_FAILED);
    }

    /**
     * 2) 결제창 띄우기용 값 생성
     */
    @Override
    public PaymentRequest init(Long memberSeq, ReservationConfirmedRequest req) {

        LocalDate.parse(req.getMentosAt());
        LocalTime.parse(req.getMentosTime());

        Mentos mentos = mentosRepository.findById(req.getMentosSeq())
                .orElseThrow(() -> new MentosException(MENTOS_NOT_FOUND));

        long amount = Math.max(0, mentos.getPrice());
        String orderName = (mentos.getMentosTitle() == null || mentos.getMentosTitle().isBlank())
                ? "멘토링 결제" : mentos.getMentosTitle();

        String orderId = makeOrderIdWithoutReservationSeq(
                req.getMentosSeq(), req.getMentosAt(), req.getMentosTime());

        String success = (memberSeq != null && successUrl.contains("{memberSeq}"))
                ? successUrl.replace("{memberSeq}", String.valueOf(memberSeq))
                : successUrl;

        return new PaymentRequest(orderId, amount, orderName, success, failUrl);
    }

    private String makeOrderIdWithoutReservationSeq(long mentosSeq, String mentosAt, String mentosTime) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String dt = (mentosAt == null ? "" : mentosAt.replace("-", "")) + "_" +
                (mentosTime == null ? "" : mentosTime.replace(":", ""));
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "ORD_M_" + mentosSeq + "_" + dt + "_" + ts + "_" + rand;
    }




    /** 성공 콜백: 토스 confirm -> 검증 -> Payment 저장 */
    @Override
    @Transactional
    public PaymentResponse confirm(Long memberSeq, String paymentKey, String orderId, long amount, ReservationConfirmedRequest req) {

        try {
            // 토스 승인(confirm) API 호출
            tossWebClient.post()
                    .uri("/v1/payments/confirm")
                    .bodyValue(Map.of(
                            "paymentKey", paymentKey,
                            "orderId", orderId,
                            "amount", amount))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res ->
                            res.bodyToMono(String.class).flatMap(body -> {
                                log.error("Toss confirm error: status={}, body={}", res.statusCode(), body);
                                return Mono.error(new MentosException(PAYMENT_FAILED));
                            })
                    )
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

        //금액 검증 (멘토스 기준)
        Mentos mentos = mentosRepository.findById(req.getMentosSeq())
                .orElseThrow(() -> new MentosException(MENTOS_NOT_FOUND));
        if (mentos.getPrice() != amount) {
            throw new MentosException(PAYMENT_FAILED);
        }

        //연관 엔티티 로딩
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MentosException(PAYMENT_FAILED));

        LocalDate date = LocalDate.parse(req.getMentosAt());
        LocalTime time = LocalTime.parse(req.getMentosTime());
        // Reservation INSERT
        Reservation reservation = Reservation.builder()
                .mentos(mentos)
                .member(member)
                .mentosAt(date)
                .mentosTime(time)
                .status(BaseStatus.ACTIVE)
                .build();
        reservationRepository.save(reservation);

        // Redis 해제 (예약 확정 후)
        seatHoldService.releaseSlot(req.getMentosSeq(), date, time);

        // 4) 승인 성공 → 결제 저장
        Payment payment = Payment.builder()
                .paymentKey(paymentKey)           // 토스에서 받은 paymentKey
                .payedAt(LocalDateTime.now())     // 결제 시각
                .price((int) amount)              // 금액
                .payType(PayType.PAID)            // 결제 타입
                .status(BaseStatus.ACTIVE)        // 상태
                .member(member)                   // 결제자
                .reservation(reservation)         // 예약
                .build();
        paymentRepository.save(payment);

        // 결제 완료 후 (성공 시) 채팅방 신규 생성
        ChattingRoom newChatRoom = ChattingRoom.create(payment);

        // 생성된 채팅방과 채팅 참여자 정보 DB에 저장
        chattingRoomRepository.save(newChatRoom);

        String dowKo = date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);

        return PaymentResponse.builder()
                .mentosTitle(mentos.getMentosTitle())
                .mentosAt(date.toString())
                .mentosTime(time.toString())
                .price(payment.getPrice())
                .dayOfWeek(dowKo)
                .build();


    }

    /** 실패 콜백*/
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

    /**
     * 환불하기
     */
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
