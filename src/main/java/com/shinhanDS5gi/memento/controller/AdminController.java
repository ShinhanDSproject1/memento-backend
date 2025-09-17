package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.admin.*;
import com.shinhanDS5gi.memento.security.CurrentUser;
import com.shinhanDS5gi.memento.service.MemberService;
import com.shinhanDS5gi.memento.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.FORBIDDEN_ACCESS;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class AdminController {

    private final ReportService reportService;
    private final MemberService memberService;

    /* 공통 - 관리자인가? */
    private void checkAdminPermission(Member admin) {
        if (admin == null || admin.getMemberType() != MemberType.ADMIN) {
            throw new AuthException(FORBIDDEN_ACCESS);
        }
    }

    /* 신고 거부하기 */
    @PatchMapping("/reports/rejection/{reportSeq}")
    public BaseResponse<Void>rejectionReport(@CurrentUser Member admin, @PathVariable("reportSeq")Long memberSeq){
        checkAdminPermission(admin); // 권한 확인
        reportService.rejectionReport(memberSeq);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 신고 승인하기 */
    @PatchMapping("/reports/approval/{reportSeq}")
    public BaseResponse<Void>approveReport(@CurrentUser Member admin, @PathVariable("reportSeq")Long memberSeq){
        checkAdminPermission(admin);
        reportService.approveReport(memberSeq);
        return new BaseResponse<>(null);
    }


    /* 관리자 페이지 전체 회원 조회하기 */
    @GetMapping("/member")
    public BaseResponse<GetMemberListResponse> getMemberList(@CurrentUser Member admin,
                                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                             @RequestParam(value = "cursor", required = false) Long cursor){
        checkAdminPermission(admin);
        log.info("[AdminController.getMemberList]");
        return new BaseResponse<>(memberService.getMemberList(limit,cursor));
    }

    /* 신고 작성하기 */
    @PostMapping(value = "/reports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> createReport(
            @CurrentUser Member admin,
            @RequestPart("requestDto") CreateReportRequest requestDto,
            @RequestPart("imageFile") MultipartFile imageFile,
            @RequestHeader("Idem-Key") String IdemKey
    ) throws IOException {
        Long currentMemberSeq = admin.getMemberSeq();
        reportService.createReport(currentMemberSeq, requestDto, imageFile, IdemKey);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 모든 신고 내역 조회 */
    @GetMapping("/reports")
    public BaseResponse<SelectReportSliceResponse<SelectReportResponse>> getAllReports(
            @CurrentUser Member admin,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Long cursor
    ) {
        checkAdminPermission(admin);
        SelectReportSliceResponse<SelectReportResponse> reportAll = reportService.findAllReports(cursor, limit);
        return new BaseResponse<>(reportAll);
    }

    /* 특정 신고 내역 상세 조회 */
    @GetMapping("/reports/{reportSeq}")
    public BaseResponse<SelectReportDetailResponse> getReportById(@CurrentUser Member admin, @PathVariable("reportSeq") Long reportSeq) {
        checkAdminPermission(admin);
        SelectReportDetailResponse report = reportService.findReportById(reportSeq);
        return new BaseResponse<>(report);
    }

    /* 회원 제명하기(관리자) */
    @PatchMapping("/member/{memberSeq}")
    public BaseResponse<Void> expelMemberByAdmin(@CurrentUser Member admin, @PathVariable("memberSeq") Long memberSeq){
        checkAdminPermission(admin);
        log.info("[AdminController.expelMemberByAdmin]");
        memberService.expelMemberByAdmin(memberSeq);
        return new BaseResponse<>(null);
    }
}
