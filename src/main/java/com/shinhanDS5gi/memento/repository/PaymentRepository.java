package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.payment.PayType;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 멤버별 결제 목록
    List<Payment> findByMember_MemberSeq(Long memberSeq);

    // 예약건 기준 결제 목록
    List<Payment> findByReservation_ReservationSeq(Long reservationSeq);

    // 예약건의 가장 최근 결제 1건
    Optional<Payment> findTopByReservation_ReservationSeqOrderByPaymentSeqDesc(Long reservationSeq);

    // 이미 결제 완료된 건 있는지 체크(중복 결제 방지 등에 활용)
    boolean existsByReservation_ReservationSeqAndPayType(Long reservationSeq, PayType payType);

    @Modifying(clearAutomatically = true)
    @Query("update Payment p set p.status = :afterStatus where p.member.memberSeq = :memberSeq and p.status = :beforeStatus")
    int updatePaymentStatus(@Param("memberSeq") Long memberSeq,
                            @Param("afterStatus") BaseStatus afterStatus,
                            @Param("beforeStatus") BaseStatus beforeStatus);
}