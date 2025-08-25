package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;

import java.util.List;

public interface ReportService {

    /* 신규 신고 생성 */
    void createReport(Long memberSeq, CreateReportRequest requestDto);

    /* 모든 신고 내역 조회 */
    List<SelectReportResponse> findAllReports();
}
