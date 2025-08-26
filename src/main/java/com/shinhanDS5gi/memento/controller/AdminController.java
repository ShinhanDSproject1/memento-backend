package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
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

    private final MemberService memberService;
    private final ReportService reportService;
    
    /* 관리자 페이지 전체 회원 조회하기 */
    @GetMapping("/member")
    public BaseResponse<GetMemberListResponse> getMemberList(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                             @RequestParam(value = "cursor", required = false) Long cursor){
        // 여기서 입력 받는 cursor 값은 Long 타입의 memberSeq
        log.info("[AdminController.getMemberList]");
        return new BaseResponse<>(memberService.getMemberList(limit,cursor));
    }
    
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
}
