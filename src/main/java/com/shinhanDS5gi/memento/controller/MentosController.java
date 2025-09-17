package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.mentos.*;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.MentosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_CREATE_MENTOS;
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
            @CurrentUser Member member,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Long cursor
    ) {
        Long currentMemberSeq = member.getMemberSeq();
        MyMentosSliceResponse<MyMentosResponse> myMentos = mentosService.getMyMentosSlice(currentMemberSeq, cursor, limit);
        return new BaseResponse<>(SUCCESS, myMentos);
    }

    /* 멘토스 게시글 수정 */
    @PutMapping("/{mentosSeq}")
    public BaseResponse<Void> updateMentos(
            @CurrentUser Member member,
            @PathVariable("mentosSeq") Long mentosSeq,
            @RequestPart("requestDto") UpdateMentosRequest requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Long currentMemberSeq = member.getMemberSeq();
        mentosService.updateMentos(mentosSeq, currentMemberSeq, requestDto, imageFile);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 멘토스 게시글 삭제 (비활성화) */
    @PatchMapping("/{mentosSeq}")
    public BaseResponse<Void> inactiveMentos(
            @CurrentUser Member member,
            @PathVariable("mentosSeq") Long mentosSeq) {
        Long currentMemberSeq = member.getMemberSeq();
        mentosService.inactiveMentos(mentosSeq, currentMemberSeq);

        return new BaseResponse<>(SUCCESS, null);
    }

    /* 멘토스 상세조회 */
    @GetMapping("/detail/{mentosSeq}")
    public BaseResponse<GetMentosDetailResponse> getMentosDetail(@PathVariable("mentosSeq") Long mentosSeq){
        log.info("[MentosController.getMentosDetail]");
        return new BaseResponse<>(mentosService.getMentosDetail(mentosSeq));
    }

    /* 멘토스 전체 조회 */
    @GetMapping("/category/{mentosCategorySeq}")
    public BaseResponse<GetMentosListResponse> getMentosList(
            @PathVariable("mentosCategorySeq") Long mentosCategorySeq,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "cursor", required = false) Long cursor){
        log.info("[MentosController.getMentosList]");
        return new BaseResponse<>(mentosService.getMentosList(mentosCategorySeq, limit, cursor));
    }

    /* 멘토스 생성하기 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> createMentos(
            @CurrentUser Member member,
            @ModelAttribute CreateMentosRequest createMentosRequest,
            @RequestHeader(value = "Idem-Key") String idemKey) throws IOException {

        log.info("[MentosController.createMentos]");

        // 프론트에서 멱등키가 같이 오지 않으면 생성이 안되도록 막기
        if(idemKey == null || idemKey.isEmpty()) {
            throw new MentosException(CANNOT_CREATE_MENTOS);
        }

        Long currentMemberSeq = member.getMemberSeq();
        mentosService.createMentos(currentMemberSeq, createMentosRequest, idemKey);

        return new BaseResponse<>(null);
    }

    /* 멘토스 수정할 때 수정 전 정보를 불러와서 보여주는 api */
    @GetMapping("/{mentosSeq}")
    public BaseResponse<ShowMentosDetailForUpdateResponse> showMentosDetailForUpdate(@CurrentUser Member member,
                                                                                     @PathVariable("mentosSeq") Long mentosSeq){
        log.info("[MentosController.showMentosDetailForUpdate]");
        return new BaseResponse<>(mentosService.showMentosDetailForUpdate(member, mentosSeq));
    }
}