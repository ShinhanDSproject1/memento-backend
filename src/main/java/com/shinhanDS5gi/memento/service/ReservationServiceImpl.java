package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.dto.reservation.GetAvailableDateResponse;
import com.shinhanDS5gi.memento.dto.reservation.MentoTimeWindowProjection;
import com.shinhanDS5gi.memento.repository.MentoProfileRepository;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MENTOS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final MentosRepository mentosRepository;
    private final SeatHoldService seatHoldService;

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    // 예약 가능한 시간 조회하기
    @Override
    public GetAvailableDateResponse getAvailableTime(Long mentosSeq, Long memberSeq, String selectedDate) {
        log.info("[ReservationServiceImpl.getAvailableTime]");

        // 입력 파싱
        final LocalDate date = LocalDate.parse(selectedDate, ISO_DATE);

        Mentos mentos = mentosRepository.findByMentosSeqAndStatus(mentosSeq, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MentosException(CANNOT_FOUND_MENTOS));

        // 1) 멘토 시간창(프로필) 조회
        MentoTimeWindowProjection w = mentosRepository
                .findTimeWindowByMentosSeqAndStatus(mentosSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_MENTOS));

        final LocalTime startTime = w.getStartTime();
        final LocalTime endTime   = w.getEndTime();
        final String availableDays = w.getAvailableDays();

        // 요청 날짜의 요일이 availableDays에 없으면 빈 배열 반환
        if (!isDayAllowed(availableDays, date.getDayOfWeek())) {
            return GetAvailableDateResponse.builder()
                    .startTime(startTime)
                    .endTime(endTime)
                    .availableTime(Collections.emptyList())
                    .build();
        }

        List<LocalTime> allSlots = hourlySlots(startTime, endTime);

        // 4) 확정 예약 슬롯 (DB 1회) - ACTIVE만 제외
        List<LocalTime> reserved = reservationRepository.findBookedTimes(
                mentosSeq, date, BaseStatus.ACTIVE
        );

        // 5) 임시 홀드 슬롯 (Redis 1회)
        List<LocalTime> held = seatHoldService.findHeldSlots(mentosSeq, date);

        // 6) 사용 불가 집합 = 확정 + 홀드
        Set<LocalTime> unavailable = new HashSet<>(reserved);
        unavailable.addAll(held);

        // 7) 가용 슬롯 = 전체 - 사용불가
        List<String> available = allSlots.stream()
                .filter(t -> !unavailable.contains(t))
                .map(t -> t.format(HHMM))
                .collect(Collectors.toList());

        // 8) 응답
        return GetAvailableDateResponse.builder()
                .mentosSeq(mentosSeq)
                .mentosTitle(mentos.getMentosTitle())
                .startTime(startTime)
                .endTime(endTime)
                .availableTime(available)
                .price(mentos.getPrice())
                .build();
    }

    private boolean isDayAllowed(String availableDays, DayOfWeek day) {
        if (availableDays == null || availableDays.isBlank()) return true;

        // 요일을 토큰셋으로 변환
        Set<String> tokens = Arrays.stream(availableDays.split(","))
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        String token = switch (day) {
            case MONDAY    -> "MON";
            case TUESDAY   -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY  -> "THU";
            case FRIDAY    -> "FRI";
            case SATURDAY  -> "SAT";
            case SUNDAY    -> "SUN";
        };
        return tokens.contains(token);
    }

    private List<LocalTime> hourlySlots(LocalTime start, LocalTime end) {
        List<LocalTime> slots = new ArrayList<>();
        for (LocalTime t = start; t.isBefore(end); t = t.plusHours(1)) {
            slots.add(t);
        }

        return slots;
    }
}
