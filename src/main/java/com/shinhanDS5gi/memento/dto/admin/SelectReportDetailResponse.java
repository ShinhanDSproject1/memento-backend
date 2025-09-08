package com.shinhanDS5gi.memento.dto.admin;

import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.domain.report.ReportType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/* 특정 신고 상세 내역 조회 */
public class SelectReportDetailResponse {

    private final Long reportSeq;
    private final ReportType reportType;
    private final String reportImage;
    private final String reporterName; // 신고자 이름
    private final String reportedMentosTitle;  // 신고된 멘토스 이름

    public static SelectReportDetailResponse from(Report report) {
        return SelectReportDetailResponse.builder()
                .reportSeq(report.getReportSeq())
                .reportType(report.getReportType())
                .reportImage(report.getReportImage())
                .reporterName(report.getMember().getMemberName())
                .reportedMentosTitle(report.getMentos().getMentosTitle())
                .build();
    }
}