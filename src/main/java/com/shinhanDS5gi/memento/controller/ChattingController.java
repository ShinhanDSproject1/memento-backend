package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.chat.*;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.security.UserAdapter;
import com.shinhanDS5gi.memento.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChattingService chattingService;

    /* 멘티 기준 나의 채팅방 목록 조회 (멘토 기준은 mentoController에) */
    @GetMapping("/chat/rooms/menti")
    public BaseResponse<List<MentiChatRoomListByMentosResponse>> getChatRoomsForMenti(@CurrentUser Member member) {
        Long currentMentiSeq = member.getMemberSeq();
        List<MentiChatRoomListByMentosResponse> chatRooms = chattingService.getChatRoomsForMenti(currentMentiSeq);
        return new BaseResponse<>(SUCCESS, chatRooms);
    }

    /* 실시간 메시지 전송 처리 */
    @MessageMapping("/chat/send")
    public void sendMessage(@Header("simpUser") Principal principal, ChattingMessageRequest messageDto) {
        log.info("채팅 메시지 수신: {}", messageDto);

        UserAdapter userAdapter = (UserAdapter) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member member = userAdapter.getMember();

        ChattingMessageResponse responseDto = chattingService.processAndSaveMessage(messageDto, member);
        messagingTemplate.convertAndSend("/topic/chat/room/" + responseDto.getChattingRoomSeq(), responseDto);
    }

    /* 채팅방 상세 정보와 이전 대화 내역 조회 */
    @GetMapping("/chat/rooms/{chattingRoomSeq}/messages")
    @ResponseBody
    public BaseResponse<ChattingRoomDetailResponse> getChattingRoomDetails( @CurrentUser Member member,
                                                                            @PathVariable Long chattingRoomSeq) {
        Long currentMemberSeq = member.getMemberSeq();
        ChattingRoomDetailResponse details = chattingService.getChattingRoomDetails(chattingRoomSeq, currentMemberSeq);
        return new BaseResponse<>(SUCCESS, details);
    }

    /* 특정 채팅방 메시지 모두 읽음 처리 (채팅방 입장 시점) */
    @PatchMapping("/chat/rooms/{chattingRoomSeq}/read")
    @ResponseBody
    public BaseResponse<Void> markAsRead(@CurrentUser Member member, @PathVariable Long chattingRoomSeq) {
        Long currentMemberSeq = member.getMemberSeq();
        chattingService.markAsRead(chattingRoomSeq, currentMemberSeq);
        return new BaseResponse<>(SUCCESS, null);
    }
}