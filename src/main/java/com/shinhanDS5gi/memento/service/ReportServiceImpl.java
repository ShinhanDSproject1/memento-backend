package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.ReportException;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.dto.CreateReportRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentosRepository;
import com.shinhanDS5gi.memento.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 1. 신고자(Member) 엔티티 조회
        Member reporter = memberRepository.findById(memberSeq).orElseThrow(()-> new MemberException(CANNOT_FOUND_MEMBER));
                //.orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        // 2. 신고 대상(Mentos) 엔티티 조회
        Mentos reportedMentos = mentosRepository.findById(requestDto.getMentosSeq())
                .orElseThrow(() -> new ReportException(CANNOT_FOUND_REPORT));

        // 3. DTO를 Report 엔티티로 변환
        Report report = requestDto.toEntity(reporter, reportedMentos);

        // 4. Report 엔티티를 데이터베이스에 저장
        Report savedReport = reportRepository.save(report);

    }

}
