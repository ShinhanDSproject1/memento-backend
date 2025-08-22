package com.shinhanDS5gi.memento.dto;

import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.domain.report.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
/* 신고 관련 RequestDTO */
public class CreateReportRequest {

    @NotNull(message = "신고 유형은 필수입니다.")
    private ReportType reportType;

    @NotNull(message = "신고 대상(멘토스) ID는 필수입니다.")
    private Long mentosSeq;

    public Report toEntity(Member member, Mentos mentos) {
        return new Report(
                null, // 자동 생성
                this.reportType,
                BaseStatus.ACTIVE,
                member,
                mentos
        );
    }
}
