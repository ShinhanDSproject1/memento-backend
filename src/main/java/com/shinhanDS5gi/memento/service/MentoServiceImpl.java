package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.dto.MyMentiResponse;
import com.shinhanDS5gi.memento.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoServiceImpl implements MentoService {

    private final ReservationRepository reservationRepository;

    /* 멘토의 멘티 목록을 멘토스 별로 그룹화해 조회 */
    @Override
    public List<MyMentiResponse> getMyMentiList(Long currentMemberId) {
        // 멘토 ID를 기준으로 모든 예약 내역을 조회
        List<Reservation> allReservations = reservationRepository.findAllByMentorId(currentMemberId);

        // 조회된 예약 내역을 Mentos를 기준으로 그룹화
        Map<Mentos, List<Reservation>> groupedByMentos = allReservations.stream()
                .collect(Collectors.groupingBy(Reservation::getMentos));

        // 그룹화된 Map을 최종 응답 DTO 리스트로 반환
        return groupedByMentos.entrySet().stream()
                .map(entry -> new MyMentiResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
