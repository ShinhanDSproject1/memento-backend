package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Reservation;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Reservation r set r.status = :afterStatus where r.member.memberSeq = :memberSeq and r.status = :beforeStatus")
    int updateReservationStatus(@Param("memberSeq") Long memberSeq,
                                @Param("afterStatus") BaseStatus afterStatus,
                                @Param("beforeStatus") BaseStatus beforeStatus);
}
