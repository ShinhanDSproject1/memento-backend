package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.common.exception.PaymentException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.payment.PayType;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import com.shinhanDS5gi.memento.dto.mentos.ReservationConfirmedRequest;
import com.shinhanDS5gi.memento.dto.payment.PaymentRequest;
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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

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

    @Override
    public void verifyReservationHolder(ReservationConfirmedRequest req, Long memberSeq) {
        LocalDate date = LocalDate.parse(req.getMentosAt());
        LocalTime time = LocalTime.parse(req.getMentosTime());
        Long holder = seatHoldService.findMemberSeqByKey(req.getMentosSeq(), date, time)
                .orElseThrow(() -> new PaymentException(PAYMENT_FAILED, "예약 선점 정보를 찾을 수 없습니다."));
        if (!holder.equals(memberSeq)) throw new PaymentException(PAYMENT_FAILED, "예약 선점자와 결제 요청자가 일치하지 않습니다.");
    }

    @Override
    public PaymentRequest init(Long memberSeq, ReservationConfirmedRequest req) {
        Mentos mentos = mentosRepository.findById(req.getMentosSeq())
                .orElseThrow(() -> new MentosException(MENTOS_NOT_FOUND, "멘토스 정보를 찾을 수 없습니다."));
        long amount = Math.max(0, mentos.getPrice());
        String orderName = (mentos.getMentosTitle() == null || mentos.getMentosTitle().isBlank())
                ? "멘토링 결제" : mentos.getMentosTitle();
        String orderId = makeOrderIdFromRequest(req);
        return new PaymentRequest(orderId, amount, orderName, successUrl, failUrl);
    }

    private String makeOrderIdFromRequest(ReservationConfirmedRequest req) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String dt = req.getMentosAt().replace("-", "") + "_" + req.getMentosTime().replace(":", "");
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "ORD_M_" + req.getMentosSeq() + "_" + dt + "_" + ts + "_" + rand;
    }

    @Override
    @Transactional
    public PaymentResponse confirm(String paymentKey, String orderId, long amount) {
        // 1. 토스 승인(confirm) API 호출
        try {
            tossWebClient.post().uri("/v1/payments/confirm")
                    .bodyValue(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res ->
                            res.bodyToMono(String.class).flatMap(body -> {
                                log.error("Toss confirm error: status={}, body={}", res.statusCode(), body);
                                return Mono.error(new PaymentException(PAYMENT_FAILED, "토스 결제 승인에 실패했습니다."));
                            }))
                    .toBodilessEntity().block();
        } catch (Exception e) {
            log.error("Toss confirm unexpected error", e);
            throw new PaymentException(PAYMENT_FAILED, "결제 승인 중 알 수 없는 오류가 발생했습니다.");
        }

        // 2. orderId로부터 예약 정보 파싱 (더 안전한 방식)
        Map<String, String> parsedInfo = parseOrderId(orderId);
        Long mentosSeq = Long.parseLong(parsedInfo.get("mentosSeq"));
        LocalDate date = LocalDate.parse(parsedInfo.get("mentosAt"));
        LocalTime time = LocalTime.parse(parsedInfo.get("mentosTime"));

        // Redis에서 예약자(memberSeq) 정보를 가져옴
        Long memberSeq = seatHoldService.findMemberSeqByKey(mentosSeq, date, time)
                .orElseThrow(() -> new PaymentException(PAYMENT_FAILED, "결제 승인 시간이 초과되었거나 유효하지 않은 주문입니다."));

        // 3. 금액 검증
        Mentos mentos = mentosRepository.findById(mentosSeq)
                .orElseThrow(() -> new MentosException(MENTOS_NOT_FOUND, "멘토스 정보를 찾을 수 없습니다."));
        if (mentos.getPrice() != amount) {
            throw new PaymentException(PAYMENT_FAILED, "결제 금액이 일치하지 않습니다.");
        }

        // 4. 연관 엔티티 로딩
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MEMBER, "회원 정보를 찾을 수 없습니다."));

        // 5. Reservation INSERT
        Reservation reservation = Reservation.builder()
                .mentos(mentos).member(member).mentosAt(date).mentosTime(time).status(BaseStatus.ACTIVE).build();
        reservationRepository.save(reservation);

        // 6. Redis 해제
        seatHoldService.releaseSlot(mentosSeq, date, time);

        // 7. Payment INSERT
        Payment payment = Payment.builder()
                .paymentKey(paymentKey).payedAt(LocalDateTime.now()).price((int) amount)
                .payType(PayType.PAID).status(BaseStatus.ACTIVE).member(member).reservation(reservation).build();
        paymentRepository.save(payment);

        // 8. 채팅방 생성
        ChattingRoom newChatRoom = ChattingRoom.create(payment);
        chattingRoomRepository.save(newChatRoom);

        String dowKo = date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);

        return PaymentResponse.builder()
                .mentosTitle(mentos.getMentosTitle()).mentosAt(date.toString())
                .mentosTime(time.toString()).price(payment.getPrice()).dayOfWeek(dowKo).build();
    }

    // orderId를 파싱하여 정보를 추출하는 헬퍼 메소드
    private Map<String, String> parseOrderId(String orderId) {
        try {
            String[] parts = orderId.split("_");
            if (parts.length != 7 || !parts[0].equals("ORD") || !parts[1].equals("M")) {
                throw new IllegalArgumentException("Invalid orderId format");
            }
            String mentosSeq = parts[2];
            String date = parts[3];
            String time = parts[4];
            String formattedDate = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
            String formattedTime = time.substring(0, 2) + ":" + time.substring(2, 4);
            return Map.of("mentosSeq", mentosSeq, "mentosAt", formattedDate, "mentosTime", formattedTime);
        } catch (Exception e) {
            log.error("Failed to parse orderId: {}", orderId, e);
            throw new PaymentException(PAYMENT_FAILED, "잘못된 주문 번호입니다.");
        }
    }

    @Override
    @Transactional
    public void fail(String code, String message, String orderId) {
        log.warn("Toss payment failed: code={}, message={}, orderId={}", code, message, orderId);
        throw new PaymentException(PAYMENT_FAILED, "결제에 실패했습니다: " + message);
    }

    //환불하기
    @Override
    @Transactional
    public void refundFull(Long currentMemberSeq, Long reservationSeq, String reason) {
        Payment payment = paymentRepository
                .findByReservation_ReservationSeqAndMember_MemberSeqAndStatus(
                        reservationSeq, currentMemberSeq, BaseStatus.ACTIVE
                )
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND, "환불할 결제 정보를 찾을 수 없습니다."));

        // (핵심 보안 검증) 해당 결제의 소유주가 환불을 요청한 사용자가 맞는지 반드시 확인
        if (!payment.getMember().getMemberSeq().equals(currentMemberSeq)) {
            throw new AuthException(FORBIDDEN_ACCESS, "해당 결제에 대한 환불 권한이 없습니다.");
        }

        tossWebClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("cancelReason", reason))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(msg -> {
                            log.error("Toss refund error: status={}, body={}", r.statusCode(), msg);
                            return Mono.error(new PaymentException(REFUND_FAILED, "토스 환불 연동에 실패했습니다."));
                        })
                )
                .toBodilessEntity()
                .block();

        payment.markRefunded();
        Reservation reservation = payment.getReservation();
        if (reservation != null) {
            reservation.deactivate();
            chattingRoomRepository.findByPayment(payment)
                    .ifPresent(ChattingRoom::deactivate);
        }
    }
}

