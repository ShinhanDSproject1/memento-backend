package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // memberSeq와 mentosSeq로 예약을 조회하는 메서드
    Optional<Reservation> findByMember_MemberSeqAndMentos_MentosSeq(Long memberSeq, Long mentosSeq);
}