package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.common.exception.ReportException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.config.S3Uploader;
import com.shinhanDS5gi.memento.domain.Mentos;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.report.Report;
import com.shinhanDS5gi.memento.domain.report.ReportHandleStatus;
import com.shinhanDS5gi.memento.dto.admin.CreateReportRequest;
import com.shinhanDS5gi.memento.dto.admin.SelectReportResponse;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.ReportRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final MentosRepository mentosRepository;
    private final MemberService memberService;
    private final S3Uploader s3Uploader;
    private final IdempotencyService idempotencyService;

    /* 신고 거부 하기 */
    @Override
    @Transactional
    public void rejectionReport(Long reportSeq) {
        //신고 조회
        Report report = reportRepository.findById(reportSeq)
                .orElseThrow(() -> new ReportException(BaseExceptionResponseStatus.CANNOT_FOUND_REPORT));
        log.info("[rejectionReport] 신고 조회 성공: 요청 reportSeq={}, 조회된 reportSeq={}",
                reportSeq, report.getReportSeq());
        //중복 처리 방지
        if (report.getHandleStatus() == ReportHandleStatus.REJECTED) {
            throw new ReportException(BaseExceptionResponseStatus.ALREADY_REJECTED_REPORT);
        }
        // 거부로 상태 변경
        report.updateHandleStatus(ReportHandleStatus.REJECTED);
    }

    /* 신고 승인 하기*/
    @Override
    @Transactional
    public void approveReport(Long reportSeq) {
        //신고 조회
        Report report = reportRepository.findById(reportSeq)
                .orElseThrow(() -> new ReportException(BaseExceptionResponseStatus.CANNOT_FOUND_REPORT));
        log.info("[rejectionReport] 신고 조회 성공: 요청 reportSeq={}, 조회된 reportSeq={}",
                reportSeq, report.getReportSeq());
        //중복 승인 방지
        if (report.getHandleStatus() == ReportHandleStatus.APPROVED) {
            throw new ReportException(BaseExceptionResponseStatus.ALREADY_APPROVED_REPORT);
        }
        //승인으로 상태 변경
        report.updateHandleStatus(ReportHandleStatus.APPROVED);
        //멤버 제명
        Long reportedMemberSeq = report.getMember().getMemberSeq();
        log.debug("[approveReport] 제명 대상 memberSeq={}", reportedMemberSeq);
        Member target = memberRepository.findByMemberSeqAndStatus(reportedMemberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        memberService.expelMemberByAdmin(target.getMemberSeq());
    }

    /* 신고 작성하기 */
    @Override
    @Transactional
    public void createReport(Long memberSeq, CreateReportRequest requestDto, MultipartFile imageFile, String idemKey) throws IOException {
        // 멱등키 중복 여부
        if (idempotencyService.isDuplicate(idemKey)){
            throw new ReportException(ALREADY_SUCCESS_REQUEST);
        }

        Member reporter = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(()-> new MemberException(CANNOT_FOUND_MEMBER));
        Mentos reportedMentos = mentosRepository.findByMentosSeqAndStatus(requestDto.getMentosSeq(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentosException(CANNOT_FOUND_REPORT));
        
        // 중복신고 방지 메서드
        if (reportRepository.existsByMember_MemberSeqAndMentos_MentosSeqAndReportTypeAndStatus(
                memberSeq, requestDto.getMentosSeq(), requestDto.getReportType(), BaseStatus.ACTIVE
        )) {
            throw new ReportException(ALREADY_REPORTED);
        }

        String imageUrl = s3Uploader.upload(imageFile); // 파일 업로드하고 URL 반환받기

        Report report = requestDto.toEntity(reporter, reportedMentos, imageUrl);
        Report savedReport = reportRepository.save(report);

        // 멱등키 Redis 저장
        idempotencyService.saveKey(idemKey, String.valueOf(savedReport.getReportSeq()));
    }

    /* 모든 신고 내역 조회 */
    @Override
    public List<SelectReportResponse> findAllReports() {
        List<Report> reports = reportRepository.findAllWithMemberAndMentos(ReportHandleStatus.PENDING);

        return reports.stream()
                .map(report -> SelectReportResponse.builder()
                        .reportSeq(report.getReportSeq())
                        .reportType(report.getReportType())
                        .reporterName(report.getMember().getMemberName())
                        .reportedMentosSeq(report.getMentos().getMentosSeq())
                        .reportImage(report.getReportImage())
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
                        .reportImage(report.getReportImage())
                        .reporterName(report.getMember().getMemberName())
                        .reportedMentosSeq(report.getMentos().getMentosSeq())
                        .createdAt(report.getCreatedAt())
                        .build())
                .orElse(null);
    }
}
