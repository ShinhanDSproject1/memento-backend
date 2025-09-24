package com.shinhanDS5gi.memento.repository.chat;

import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

    /* 멘토가 참여하고 있는 모든 채팅방을 마지막 메시지 시간 순서대로 정렬하여 조회 (채팅방 목록)*/
    @Query("SELECT DISTINCT cr FROM ChattingRoom cr " +
            "JOIN FETCH cr.participants p " +
            "JOIN FETCH p.member " +
            "JOIN FETCH cr.payment pay " +
            "JOIN FETCH pay.reservation r " +
            "JOIN FETCH r.mentos m " +
            "WHERE m.member.memberSeq = :mentorSeq " +
            "ORDER BY cr.lastMessageAt DESC")
    List<ChattingRoom> findAllByMentorIdGroupedByMentos(@Param("mentorSeq") Long mentorSeq);

    @Query("SELECT DISTINCT cr.chattingRoomSeq FROM ChattingRoom cr " +
            "JOIN cr.participants p " +
            "WHERE p.member.memberSeq = :menteeSeq AND cr.status = 'ACTIVE'")
    List<Long> findActiveChattingRoomIdsByMentiId(@Param("menteeSeq") Long menteeSeq);

    // ID 목록으로 모든 정보를 한번에 조회하는 쿼리
    @Query("SELECT DISTINCT cr FROM ChattingRoom cr " +
            "JOIN FETCH cr.participants p " +
            "JOIN FETCH p.member mem " +
            "LEFT JOIN FETCH mem.mentoProfile " +
            "JOIN FETCH cr.payment pay " +
            "JOIN FETCH pay.reservation r " +
            "JOIN FETCH r.mentos m " +
            "WHERE cr.chattingRoomSeq IN :roomIds " +
            "ORDER BY cr.lastMessageAt DESC")
    List<ChattingRoom> findAllWithDetailsByRoomIds(@Param("roomIds") List<Long> roomIds);

    /* 채팅방 상세정보 조회 (채팅방 내부) */
    @Query("SELECT cr FROM ChattingRoom cr " +
            "JOIN FETCH cr.participants p " +
            "JOIN FETCH p.member " +
            "WHERE cr.chattingRoomSeq = :chattingRoomSeq")
    Optional<ChattingRoom> findByIdWithParticipants(@Param("chattingRoomSeq") Long chattingRoomSeq);

    //환불할때 inactive
    Optional<ChattingRoom> findByPayment(Payment payment);
}