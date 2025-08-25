package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.MentosDetailResponse;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;
import com.shinhanDS5gi.memento.service.MentosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentos")
public class MentosController {

    private final MentosService mentosService;

    /* 멘토스 기존 정보 조회 */
    @GetMapping("/{mentosSeq}")
    public BaseResponse<MentosDetailResponse> getMentosForUpdate(@PathVariable("mentosSeq") Long mentosSeq) {
        MentosDetailResponse mentos = mentosService.getMentosById(mentosSeq);
        return new BaseResponse<>(SUCCESS, mentos);
    }

    /* 멘토스 게시글 수정 */
    @PutMapping("/{mentosSeq}")
    public BaseResponse<Void> updateMentos(@PathVariable("mentosSeq") Long mentosSeq, @RequestBody UpdateMentosRequest requestDto) {

        Long currentMemberId = 2L;
        mentosService.updateMentos(mentosSeq, currentMemberId, requestDto);

        return new BaseResponse<>(SUCCESS, null);
    }
}