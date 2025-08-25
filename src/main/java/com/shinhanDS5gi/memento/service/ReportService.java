package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.dto.CreateReportRequest;

public interface ReportService {

    /* 신규 신고 생성 */
    void createReport(Long memberSeq, CreateReportRequest requestDto);

}
