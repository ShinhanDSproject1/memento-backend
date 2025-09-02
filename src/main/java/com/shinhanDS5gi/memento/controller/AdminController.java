package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.admin.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.admin.SelectReportResponse;
import com.shinhanDS5gi.memento.service.MemberService;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.service.ReportService;
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
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;
    private final MemberService memberService;

    /* 신고 거부하기 */
    @PatchMapping("/reports/rejection/{reportSeq}")
    public BaseResponse<Void>rejectionReport(@PathVariable("reportSeq")Long memberSeq){
        reportService.rejectionReport(memberSeq);
        return new BaseResponse<>(null);
    }

    /* 신고 승인하기 */
    @PatchMapping("/reports/approval/{reportSeq}")
    public BaseResponse<Void>approveReport(@PathVariable("reportSeq")Long memberSeq){
        reportService.approveReport(memberSeq);
        return new BaseResponse<>(null);
    }


    /* 관리자 페이지 전체 회원 조회하기 */
    @GetMapping("/member")
    public BaseResponse<GetMemberListResponse> getMemberList(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                             @RequestParam(value = "cursor", required = false) Long cursor){
        // 여기서 입력 받는 cursor 값은 Long 타입의 memberSeq
        log.info("[AdminController.getMemberList]");
        return new BaseResponse<>(memberService.getMemberList(limit,cursor));
    }

    /* 신고 작성하기 */
    @PostMapping(value = "/reports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> createReport(
            @RequestPart("requestDto") CreateReportRequest requestDto,
            @RequestPart("imageFile") MultipartFile imageFile
    ) throws IOException {
        Long currentMemberSeq = 1L;
        reportService.createReport(currentMemberSeq, requestDto, imageFile);
        return new BaseResponse<>(SUCCESS, null);
    }

    /* 모든 신고 내역 조회 */
    @GetMapping("/reports")
    public BaseResponse<List<SelectReportResponse>> getAllReports() {
        List<SelectReportResponse> reportAll = reportService.findAllReports();
        return new BaseResponse<>(reportAll);
    }

    /* 특정 신고 내역 상세 조회 */
    @GetMapping("/reports/{reportSeq}")
    public BaseResponse<SelectReportResponse> getReportById(@PathVariable("reportSeq") Long reportSeq) {
        SelectReportResponse report = reportService.findReportById(reportSeq);
        return new BaseResponse<>(report);
    }

    /* 회원 제명하기(관리자) */
    @PatchMapping("/member/{memberSeq}")
    public BaseResponse<Void> expelMemberByAdmin(@PathVariable("memberSeq") Long memberSeq){
        log.info("[AdminController.expelMemberByAdmin]");
        // 여기에 member 검증하는 부분이 나와야함
        memberService.expelMemberByAdmin(memberSeq);
        return new BaseResponse<>(null);
    }
}
