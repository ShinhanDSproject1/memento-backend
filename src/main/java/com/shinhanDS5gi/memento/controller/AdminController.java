package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;
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

}
