package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.domain.report.ReportType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SelectReportResponse {

    private final Long reportSeq;
    private final ReportType reportType;
    private final String reporterName; // 신고자 이름
    private final Long reportedMentosSeq;  // 신고된 멘토스 시퀀스
    private final LocalDateTime createdAt; // 신고 생성 시간

}