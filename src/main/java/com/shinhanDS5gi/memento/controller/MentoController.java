package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.mento.*;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.MentoCertificationService;
import com.shinhanDS5gi.memento.service.MentoProfileService;
import com.shinhanDS5gi.memento.service.MentoService;
import com.shinhanDS5gi.memento.service.ReviewService;
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

    private final MentoService mentoService;
    private final MentoCertificationService mentoCertificationService;
    private final MentoProfileService mentoProfileService;
    private final ReviewService reviewService;

    /* 멘토 리뷰 조회 */
    @GetMapping("/reviews/{mentorSeq}")
    public BaseResponse<MentoReviewsSliceResponse<MentoReviewsListResponse>> getMentoReviews(
            @PathVariable("mentorSeq") Long mentorSeq,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long cursor
    ) {
        var page = reviewService.getMentoReviews(mentorSeq, limit, cursor);
        return new BaseResponse<>(SUCCESS, page);
    }

    /* 멘토 자격증 추가 */
    @PostMapping(value = "/mento-certifications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> createMentoCertification(
            @CurrentUser Member member,
            @RequestPart("requestDto") CreateMentoCertificationRequest requestDto,
            @RequestPart("imageFile") MultipartFile imageFile,
            @RequestHeader("Idem-Key") String idemKey) throws IOException {

        Long currentMemberSeq = member.getMemberSeq();
        mentoCertificationService.createMentoCertification(currentMemberSeq, requestDto, imageFile, idemKey);

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
                                                 @RequestPart("requestDto") CreateMentoProfileRequest requestDto,
                                                 @RequestPart("imageFile") MultipartFile imageFile,
                                                 @RequestHeader("Idem-Key") String idemKey) throws IOException {

        Long currentMemberSeq = member.getMemberSeq();
        mentoProfileService.createMentoProfile(currentMemberSeq, requestDto, imageFile, idemKey);

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

    /* 멘티 조회 (멘토스별 조회) */
    @GetMapping("/menti-list")
    public BaseResponse<List<MyMentiResponse>> getMyMentiList(@CurrentUser Member member) {
        Long currentMemberSeq = member.getMemberSeq();
        List<MyMentiResponse> mentiList = mentoService.getMyMentiList(currentMemberSeq);
        return new BaseResponse<>(SUCCESS, mentiList);
    }
}
