package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;

import java.util.List;

public interface ReportService {

    /* 신고 거부하기 */
    void rejectionReport(Long reportSeq);

    /* 신고 승인하기 */
    void approveReport(Long reportSeq);

    /* 신규 신고 작성 */
    void createReport(Long memberSeq, CreateReportRequest requestDto);

    /* 모든 신고 내역 조회 */
    List<SelectReportResponse> findAllReports();

    /* 특정 신고 상세 내역 조회 */
    SelectReportResponse findReportById(Long reportSeq);
}
