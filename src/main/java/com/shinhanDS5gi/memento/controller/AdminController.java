package com.shinhanDS5gi.memento.controller;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.SUCCESS;

import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;

    @PostMapping("/reports")
    public BaseResponse<Void> createReport(@RequestBody CreateReportRequest requestDto) {

        /* 임의로 memberId 설정 */
        Long currentMemberId = 1L;
        reportService.createReport(currentMemberId, requestDto);
        return new BaseResponse<>(SUCCESS, null);
    }

}