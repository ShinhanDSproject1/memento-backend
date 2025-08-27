package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.ReportException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentosRepository;
import com.shinhanDS5gi.memento.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_REPORT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final MentosRepository mentosRepository;

    /* 신고 작성하기 */
    @Override
    @Transactional
    public void createReport(Long memberSeq, CreateReportRequest requestDto) {
        Member reporter = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberException(CANNOT_FOUND_MEMBER));
        Mentos reportedMentos = mentosRepository.findByMentosSeqAndStatus(requestDto.getMentosSeq(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new ReportException(CANNOT_FOUND_REPORT));
        Report report = requestDto.toEntity(reporter, reportedMentos);
        reportRepository.save(report);
    }

    /* 모든 신고 내역 조회 */
    @Override
    public List<SelectReportResponse> findAllReports() {
        List<Report> reports = reportRepository.findAllWithMemberAndMentos();
        return reports.stream()
                .map(report -> SelectReportResponse.builder()
                        .reportSeq(report.getReportSeq())
                        .reportType(report.getReportType())
                        .reporterName(report.getMember().getMemberName())
                        .reportedMentosSeq(report.getMentos().getMentosSeq())
                        .createdAt(report.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /* 특정 신고 내역 상세 조회 */
    @Override
    public SelectReportResponse findReportById(Long reportSeq) {
        return reportRepository.findByIdWithMemberAndMentos(reportSeq)
                .map(report -> SelectReportResponse.builder()
                        .reportSeq(report.getReportSeq())
                        .reportType(report.getReportType())
                        .reporterName(report.getMember().getMemberName())
                        .reportedMentosSeq(report.getMentos().getMentosSeq())
                        .createdAt(report.getCreatedAt())
                        .build())
                .orElse(null);
    }
}
