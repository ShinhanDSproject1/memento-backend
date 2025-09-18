package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.chat.ChattingRoomListByMentosResponse;
import com.shinhanDS5gi.memento.dto.mento.*;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mento")
public class MentoController {

    private final MentoCertificationService mentoCertificationService;
    private final MentoProfileService mentoProfileService;
    private final ReviewService reviewService;
    private final ChattingService chattingService;

    /* 멘토 리뷰 조회 */
    @GetMapping("/reviews")
    public BaseResponse<MentoReviewsSliceResponse<MentoReviewsListResponse>> getMentoReviews(
            @CurrentUser Member member,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long cursor
    ) {
        log.info("[MentoController.getMentoReviews]");
        Long currentMemberSeq = member.getMemberSeq(); // 멘토
        return new BaseResponse<>(reviewService.getMentoReviews(currentMemberSeq, limit, cursor));

    }

    /* 멘토 자격증 추가 */
    @PostMapping(value = "/mento-certifications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> createMentoCertification(
            @CurrentUser Member member,
            @ModelAttribute CreateMentoCertificationRequest requestDto,
            @RequestHeader("Idem-Key") String idemKey) {

        log.info("[MentoController.createMentoCertification]");
        Long currentMemberSeq = member.getMemberSeq();
        mentoCertificationService.createMentoCertification(currentMemberSeq, requestDto.getCertificationName(), requestDto.getCertificationImgUrl(), idemKey);

        return new BaseResponse<>(SUCCESS, null);
    }

    /* 멘토 보유 자격증 목록 조회 */
    @GetMapping("/mento-certifications")
    public BaseResponse<List<MentoCertificationsResponse>> getMentoCertifications(@CurrentUser Member member) {
        Long currentMemberSeq = member.getMemberSeq();
        List<MentoCertificationsResponse> certifications = mentoCertificationService.getMentoCertifications(currentMemberSeq);

        return new BaseResponse<>(SUCCESS, certifications);
    }

    /* 멘토 프로필 생성 */
    @PostMapping(value = "/mento-profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> createMentoProfile(@CurrentUser Member member,
                                                 @ModelAttribute CreateMentoProfileRequest requestDto,
                                                 @RequestHeader("Idem-Key") String idemKey) throws IOException {

        Long currentMemberSeq = member.getMemberSeq();
        mentoProfileService.createMentoProfile(currentMemberSeq, requestDto);

        return new BaseResponse<>(SUCCESS, null);
    }

    /* 멘토 프로필 수정 */
    @PatchMapping(value = "/mento-profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> updateMentoProfile(
            @CurrentUser Member member,
            @RequestPart("requestDto") UpdateMentoProfileRequest requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Long currentMemberSeq = member.getMemberSeq();
        mentoProfileService.updateMentoProfile(currentMemberSeq, requestDto, imageFile);

        return new BaseResponse<>(SUCCESS, null);
    }

    /* 멘토 프로필 조회 */
    @GetMapping("/mento-profiles")
    public BaseResponse<MentoProfileResponse> getMentoProfile(@CurrentUser Member member) {
        Long currentMemberSeq = member.getMemberSeq();
        MentoProfileResponse profile = mentoProfileService.getMentoProfile(currentMemberSeq);
        return new BaseResponse<>(SUCCESS, profile);
    }

    /* 멘토의 채팅방 목록을 멘토스별로 그룹화하여 조회 */
    @GetMapping("/menti-list")
    public BaseResponse<List<ChattingRoomListByMentosResponse>> getMyChatRoomList(@CurrentUser Member member) {
        Long currentMentorSeq = member.getMemberSeq();
        List<ChattingRoomListByMentosResponse> chatRoomList = chattingService.getChatRoomsByMentosForMentor(currentMentorSeq);
        return new BaseResponse<>(SUCCESS, chatRoomList);
    }
}
