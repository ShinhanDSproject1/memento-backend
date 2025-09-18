package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;

import com.shinhanDS5gi.memento.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Payment p " +
            "set p.status = :afterStatus " +
            "where p.member.memberSeq = :memberSeq and p.status = :beforeStatus")
    int updatePaymentStatus(@Param("memberSeq") Long memberSeq,
                            @Param("afterStatus") BaseStatus afterStatus,
                            @Param("beforeStatus") BaseStatus beforeStatus);

    Optional<Payment> findByReservation_ReservationSeqAndMember_MemberSeqAndStatus(
            Long reservationSeq, Long memberSeq, BaseStatus status);

}