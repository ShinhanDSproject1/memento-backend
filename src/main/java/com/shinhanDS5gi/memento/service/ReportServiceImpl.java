package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.ReportException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.SelectReportResponse;
import com.shinhanDS5gi.memento.repository.MemberRepository;
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

    @Override
    @Transactional
    public void createReport(Long memberSeq, CreateReportRequest requestDto) {
        Member reporter = memberRepository.findById(memberSeq).orElseThrow(()-> new MemberException(CANNOT_FOUND_MEMBER));
        Mentos reportedMentos = mentosRepository.findById(requestDto.getMentosSeq())
                .orElseThrow(() -> new ReportException(CANNOT_FOUND_REPORT));
        Report report = requestDto.toEntity(reporter, reportedMentos);
        reportRepository.save(report);
    }

    @Override
    public List<SelectReportResponse> findAllReports() {
        List<Report> reports = reportRepository.findAllWithMemberAndMentos();
        return reports.stream()
                .map(SelectReportResponse::new)
                .collect(Collectors.toList());
    }

}
