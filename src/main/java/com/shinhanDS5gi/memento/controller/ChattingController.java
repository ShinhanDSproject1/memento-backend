package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.chat.ChattingMessageRequest;
import com.shinhanDS5gi.memento.dto.chat.ChattingMessageResponse;
import com.shinhanDS5gi.memento.dto.chat.ChattingRoomDetailResponse;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChattingService chattingService;

    /* 실시간 메시지 전송 처리 */
    @MessageMapping("/chat/send")
    public void sendMessage(ChattingMessageRequest messageDto) {

        // 서비스에 메시지를 전달해 DB에 저장 및 응답
        ChattingMessageResponse responseDto = chattingService.processAndSaveMessage(messageDto);

        // 메시지 브로커를 통해 해당 채팅방을 구독하는 모든 클라이언트에게 메시지 전송
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