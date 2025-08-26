package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;
import com.shinhanDS5gi.memento.service.MemberService;
import com.shinhanDS5gi.memento.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;
    private final MemberService memberService;
    
    /* 신고 작성하기 */
    @PostMapping("/reports")
    public BaseResponse<Void> createReport(@RequestBody CreateReportRequest requestDto) {
        Long currentMemberId = 1L;
        reportService.createReport(currentMemberId, requestDto);
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
