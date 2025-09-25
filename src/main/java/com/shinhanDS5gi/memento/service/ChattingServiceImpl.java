package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.BaseException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.chat.ChattingMessageDocument;
import com.shinhanDS5gi.memento.domain.chat.ChattingParticipant;
import com.shinhanDS5gi.memento.domain.chat.ChattingRoom;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.chat.*;
import com.shinhanDS5gi.memento.repository.mento.MentoProfileRepository;
import com.shinhanDS5gi.memento.repository.chat.ChattingMessageRepository;
import com.shinhanDS5gi.memento.repository.chat.ChattingParticipantRepository;
import com.shinhanDS5gi.memento.repository.chat.ChattingRoomRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChattingServiceImpl implements ChattingService {

    private static final Logger log = LoggerFactory.getLogger(ChattingServiceImpl.class);

    private final ChattingMessageRepository chattingMessageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingParticipantRepository chattingParticipantRepository;
    private final MemberRepository memberRepository;
    private final MentoProfileRepository mentoProfileRepository;

    // 멘티 고정 프로필 이미지 (무슨 사진 넣을지 논의 필요)
    private static final String MENTI_DEFAULT_IMAGE = "";

    @Override
    @Transactional
    public ChattingMessageResponse processAndSaveMessage(ChattingMessageRequest messageDto, Member sender) {
        String profileImage = getProfileImage(sender);

        ChattingMessageDocument chatMessage = ChattingMessageDocument.builder()
                .chattingRoomSeq(messageDto.getChattingRoomSeq())
                .senderSeq(sender.getMemberSeq())
                .senderName(sender.getMemberName())
                .senderProfileImage(profileImage)
                .message(messageDto.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        chattingMessageRepository.save(chatMessage);

        ChattingRoom room = chattingRoomRepository.findById(messageDto.getChattingRoomSeq())
                .orElseThrow(() -> new BaseException(BaseExceptionResponseStatus.CANNOT_FOUND_CHAT_ROOM));
        room.updateLastMessage(messageDto.getMessage(), chatMessage.getCreatedAt());

        List<ChattingParticipant> participants = chattingParticipantRepository.findByChattingRoom_ChattingRoomSeq(room.getChattingRoomSeq());
        for (ChattingParticipant participant : participants) {
            if (participant.getMember().getMemberSeq().equals(sender.getMemberSeq())) {
                // 메시지를 보낸 사람: '읽음'으로 처리
                participant.markAsRead();
            } else {
                // 메시지를 받는 사람: '안 읽음'으로 처리
                participant.markAsUnread();
            }
        }

        return ChattingMessageResponse.builder()
                .chattingRoomSeq(chatMessage.getChattingRoomSeq())
                .senderSeq(chatMessage.getSenderSeq())
                .senderName(chatMessage.getSenderName())
                .senderProfileImage(chatMessage.getSenderProfileImage())
                .message(chatMessage.getMessage())
                .sentAt(chatMessage.getCreatedAt())
                .build();
    }

    /* 채팅방 상세 정보 및 이전 대화 내역 조회 */
    @Override
    @Transactional(readOnly = true)
    public ChattingRoomDetailResponse getChattingRoomDetails(Long chattingRoomSeq, Long currentMemberSeq) {

        ChattingRoom room = chattingRoomRepository.findByIdWithParticipants(chattingRoomSeq)
                .orElseThrow(() -> new BaseException(BaseExceptionResponseStatus.CANNOT_FOUND_CHAT_ROOM));

        boolean isParticipant = room.getParticipants().stream()
                .anyMatch(p -> p.getMember().getMemberSeq().equals(currentMemberSeq));
        if (!isParticipant) {
            throw new BaseException(BaseExceptionResponseStatus.FORBIDDEN_ACCESS);
        }

        // 참여자 프로필 한 번에 조회
        List<ChattingRoomDetailResponse.ParticipantInfo> participants = room.getParticipants().stream()
                .map(p -> {
                    Member member = p.getMember();
                    String profileImg = getProfileImage(member);
                    return ChattingRoomDetailResponse.ParticipantInfo.builder()
                            .memberSeq(member.getMemberSeq())
                            .memberName(member.getMemberName())
                            .profileImage(profileImg)
                            .build();
                })
                .collect(Collectors.toList());

        // Mongo DB에서 메시지 내역 조회 및 반환
        List<ChattingMessageResponse> messages = chattingMessageRepository.findByChattingRoomSeqOrderByCreatedAtAsc(chattingRoomSeq)
                .stream()
                .map(msg -> ChattingMessageResponse.builder()
                        .chattingRoomSeq(msg.getChattingRoomSeq())
                        .senderSeq(msg.getSenderSeq())
                        .senderName(msg.getSenderName())
                        .senderProfileImage(msg.getSenderProfileImage())
                        .message(msg.getMessage())
                        .sentAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ChattingRoomDetailResponse.builder()
                .chattingRoomSeq(room.getChattingRoomSeq())
                .mentosTitle(room.getPayment().getReservation().getMentos().getMentosTitle())
                .participants(participants)
                .messages(messages)
                .build();
    }

    // 프로필 이미지 가져오기
    private String getProfileImage(Member member) {
        if (member.getMemberType() == MemberType.MENTO) {
            return mentoProfileRepository.findByMember_MemberSeq(member.getMemberSeq())
                    .map(MentoProfile::getMentoProfileImage)
                    .orElse(null); // 멘토인데 프로필이 없는 경우... 일단 null
        } else {
            return MENTI_DEFAULT_IMAGE; // 멘티는 기본 이미지
        }
    }

    // 멘토의 채팅방 목록 보기
    @Override
    @Transactional(readOnly = true)
    public List<ChattingRoomListByMentosResponse> getChatRoomsByMentosForMentor(Long mentorId) {
        List<ChattingRoom> allChatRooms = chattingRoomRepository.findAllByMentorIdGroupedByMentos(mentorId);
        Map<Mentos, List<ChattingRoom>> groupedByMentos = allChatRooms.stream()
                .collect(Collectors.groupingBy(room -> room.getPayment().getReservation().getMentos()));

        return groupedByMentos.entrySet().stream()
                .map(entry -> new ChattingRoomListByMentosResponse(
                        entry.getKey().getMentosSeq(),
                        entry.getKey().getMentosTitle(),
                        entry.getValue(),
                        mentorId
                ))
                .collect(Collectors.toList());
    }

    // 멘티의 채팅방 목록 보기
    @Override
    @Transactional(readOnly = true)
    public List<MentiChatRoomListByMentosResponse> getChatRoomsForMenti(Long mentiId) {
        log.info("Fetching chat rooms for mentiId: {}", mentiId);

        // 채팅방 ID 목록 조회
        List<Long> roomIds = chattingRoomRepository.findActiveChattingRoomIdsByMentiId(mentiId);

        // 참여 중인 채팅방이 없으면 빈 목록 반환
        if (roomIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ID 목록으로 채팅방 상세 정보 전체 조회
        List<ChattingRoom> allChatRooms = chattingRoomRepository.findAllWithDetailsByRoomIds(roomIds);

        Map<Mentos, List<ChattingRoom>> groupedByMentos = allChatRooms.stream()
                .collect(Collectors.groupingBy(room -> room.getPayment().getReservation().getMentos()));

        return groupedByMentos.entrySet().stream()
                .map(entry -> new MentiChatRoomListByMentosResponse(
                        entry.getKey().getMentosSeq(),
                        entry.getKey().getMentosTitle(),
                        entry.getValue(),
                        mentiId
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long chattingRoomSeq, Long memberSeq) {
        // 참여 정보 조회 (채팅방 ID, 사용자 ID)
        ChattingParticipant participant = chattingParticipantRepository.findByChattingRoom_ChattingRoomSeqAndMember_MemberSeq(chattingRoomSeq, memberSeq)
                .orElseThrow(() -> new BaseException(BaseExceptionResponseStatus.CANNOT_FOUND_CHAT_PARTICIPANT));

        // ChattingParticipant 엔티티의 읽음 처리
        participant.markAsRead();
    }
}