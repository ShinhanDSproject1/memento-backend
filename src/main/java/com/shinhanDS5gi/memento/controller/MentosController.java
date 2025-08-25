package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.MyMentosResponse;
import com.shinhanDS5gi.memento.dto.MyMentosSliceResponse;
import com.shinhanDS5gi.memento.dto.UpdateMentosRequest;
import com.shinhanDS5gi.memento.service.MentosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentos")
public class MentosController {

    private final MentosService mentosService;

    /* 나의 멘토스 목록 조회 (멘토) */
    @GetMapping("/my-list")
    public BaseResponse<MyMentosSliceResponse<MyMentosResponse>> getMyMentos(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Long cursor
    ) {
        Long currentMemberId = 1L; // 임시 사용자 ID
        MyMentosSliceResponse<MyMentosResponse> myMentos = mentosService.getMyMentosSlice(currentMemberId, cursor, limit);
        return new BaseResponse<>(SUCCESS, myMentos);
    }

    /* 멘토스 게시글 수정 */
    @PutMapping("/{mentosSeq}")
    public BaseResponse<Void> updateMentos(@PathVariable("mentosSeq") Long mentosSeq, @RequestBody UpdateMentosRequest requestDto) {
        Long currentMemberId = 1L;
        mentosService.updateMentos(mentosSeq, currentMemberId, requestDto);

        return new BaseResponse<>(SUCCESS, null);
    }

    /* 멘토스 게시글 삭제 (비활성화) */
    @PatchMapping("/{mentosSeq}")
    public BaseResponse<Void> inactiveMentos(@PathVariable("mentosSeq") Long mentosSeq) {
        Long currentMemberId = 1L;
        mentosService.inactiveMentos(mentosSeq, currentMemberId);

        return new BaseResponse<>(SUCCESS, null);
    }
}