package com.shinhanDS5gi.memento.service;


import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.mento.CreateMentoCertificationRequest;
import com.shinhanDS5gi.memento.dto.auth.MentoSignupRequest;
import com.shinhanDS5gi.memento.dto.auth.MentiSignupRequest;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.repository.mento.MentoCertificationRepository;
import com.shinhanDS5gi.memento.repository.*;
import com.shinhanDS5gi.memento.repository.review.ReviewRepository;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.mento.MentoProfileRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder pwEncoder;
    private final MentoCertificationRepository certRepository;
    private final MentosRepository mentosRepository;
    private final MentoProfileRepository mentoProfileRepository;
    private final ReviewRepository reviewRepository;
    private final ReportRepository reportRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final MentoCertificationService mentoCertificationService;
    private final IdempotencyService idempotencyService;

    private final AuthService authService;


    /* 관리자 페이지 전체 회원 조회하기 */
    @Override
    public GetMemberListResponse getMemberList(Integer limit, Long cursor) {
        log.info("[MemberServiceImpl.getMemberList]");

        List<Member> memberList = memberRepository.findAllByIdAndLimitAndCursor(limit, cursor, BaseStatus.ACTIVE);

        List<GetMemberListResponse.MemberInfo> memberInfoList = memberList.stream().map(member -> new GetMemberListResponse.MemberInfo(
                member.getMemberSeq(), member.getMemberName(), member.getMemberType().toString(), member.getCreatedAt().toLocalDate()
        )).limit(limit).toList();

        GetMemberListResponse result;
        if (memberList.size() <= limit) {
            result = GetMemberListResponse.builder().members(memberInfoList).hasNext(false).build();
        } else {
            result = GetMemberListResponse.builder().members(memberInfoList).hasNext(true).build();
        }
        return result;
    }

    /* 회원탈퇴 */
    @Override
    @Transactional
    public void withdraw(Long memberSeq, HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        //ACTIVE인 회원만 찾음 → 없거나 이미 INACTIVE면 바로 오류메세지
        log.info("[MemberServiceImpl.expelMemberByAdmin]");
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        // member 테이블 무효화 (영속성 컨텍스트 / 1차 캐시를 이용하려고 따로 save 메소드 호출하지 않음)
        member.updateMemberStatus(BaseStatus.INACTIVE);

        if (member.getMemberType().equals(MemberType.MENTO)) {
            expelForMento(member.getMemberSeq());
        } else if (member.getMemberType().equals(MemberType.MENTI)) {
            expelForMenti(member.getMemberSeq());
        }
        //AT블랙리스트 + RT제거 + cookie 제거
        authService.cleanupTokensAndCookies(req, res, secureCookie);
    }

    /* 멘토 회원가입 */
    @Override
    @Transactional
    public void signupMento(MentoSignupRequest req, @Nullable MultipartFile certImage, String idemKey) throws IOException {
        // 멱등키 중복 여부 (추가)
        if (idempotencyService.isDuplicate(idemKey)) {
            throw new MemberException(ALREADY_SUCCESS_REQUEST);
        }

        // 1) 중복 아이디 체크
        if (memberRepository.existsByMemberId(req.getMemberId())) {
            log.warn("[signupMento] 중복 아이디: {}", req.getMemberId());
            throw new MemberException(CANNOT_SIGNUP);
        }
        // 2) 생년월일 파싱 (yyyy-MM-dd)
        LocalDate birth = LocalDate.parse(req.getMemberBirthDate());
        // 3) 엔티티에 회원 저장 (MENTO)
        Member member = Member.builder()
                .memberId(req.getMemberId())
                .memberPwd(pwEncoder.encode(req.getMemberPwd()))
                .memberName(req.getMemberName())
                .memberPhoneNumber(req.getMemberPhoneNumber())
                .memberBirthDate(birth)
                .memberType(MemberType.MENTO)
                .status(BaseStatus.ACTIVE)
                .build();
        Member savedMember = memberRepository.save(member);

        // 4) 자격증 저장
        boolean hasName = req.getCertificationName() != null && !req.getCertificationName().isBlank();
        boolean hasFile = certImage != null && !certImage.isEmpty();

        if (hasName && hasFile) {
            CreateMentoCertificationRequest certReq = CreateMentoCertificationRequest.builder()
                    .name(req.getCertificationName())
                    .build();
            mentoCertificationService.createMentoCertification(member.getMemberSeq(), certReq, certImage, idemKey);
            log.info("[signupMento] 자격증 등록 완료: {}", req.getCertificationName());
        } else if (!hasName && !hasFile) {
            log.info("[signupMento] 자격증 없이 가입 완료");
        }

        // 멱등키 Redis 저장
        idempotencyService.saveKey(idemKey, String.valueOf(savedMember.getMemberSeq()));
    }

    /* 멘티 회원가입 */
    @Override
    @Transactional
    public void signupMenti(MentiSignupRequest req, String idemKey) {
        // 멱등키 중복 검사
        if (idempotencyService.isDuplicate(idemKey)) {
            throw new MemberException(ALREADY_SUCCESS_REQUEST);
        }

        // 1) 중복 아이디 체크
        if (memberRepository.existsByMemberId(req.getMemberId())) {
            log.warn("[signupMenti] 중복 아이디: {}", req.getMemberId());
            throw new MemberException(CANNOT_SIGNUP);
        }
        // 2) 생년월일 파싱 (yyyy-MM-dd)
        LocalDate birth = LocalDate.parse(req.getMemberBirthDate());
        // 3) 엔티티에 회원 저장 (MENTI)
        Member m = Member.builder()
                .memberId(req.getMemberId())
                .memberPwd(pwEncoder.encode(req.getMemberPwd()))
                .memberName(req.getMemberName())
                .memberPhoneNumber(req.getMemberPhoneNumber())
                .memberBirthDate(birth)
                .memberType(MemberType.MENTI)
                .status(BaseStatus.ACTIVE)
                .build();

        Member savedMember = memberRepository.save(m);

        // 멱등키 Redis 저장
        idempotencyService.saveKey(idemKey, String.valueOf(savedMember.getMemberSeq()));
    }

    /* 회원 제명하기 (관리자) */
    @Transactional
    @Override
    public void expelMemberByAdmin(Long memberSeq) {
        log.info("[MemberServiceImpl.expelMemberByAdmin]");
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE).orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
        // member 테이블 무효화 (영속성 컨텍스트 / 1차 캐시를 이용하려고 따로 save 메소드 호출하지 않음)
        member.updateMemberStatus(BaseStatus.INACTIVE);

        if (member.getMemberType().equals(MemberType.MENTO)) {
            expelForMento(member.getMemberSeq());
        } else if (member.getMemberType().equals(MemberType.MENTI)) {
            expelForMenti(member.getMemberSeq());
        }

    }

    private void expelForMento(Long memberSeq) {
        // mento 인 경우 -> 자격증 검증 테이블, 멘토스, 멘토 프로필 무효 처리
        log.info("[MemberServiceImpl.expelForMento]");

        int certificationChangeCnt = certRepository.updateMentoCertificationStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMento...updateMentoCertificationStatus..." + certificationChangeCnt + "]");

        int mentosChangeCnt = mentosRepository.updateMentosStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMento...updateMentosStatus..." + mentosChangeCnt + "]");

        int mentoProfileChangeCnt = mentoProfileRepository.updateMentoProfileStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMento...updateMentoProfileStatus..." + mentoProfileChangeCnt + "]");
    }

    private void expelForMenti(Long memberSeq) {
        // menti 인 경우 -> 리뷰, 신고, 예약, 결제 무효 처리
        log.info("[MemberServiceImpl.expelForMenti]");

        int reviewChangeCnt = reviewRepository.updateReviewStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMenti...updateReviewStatus..." + reviewChangeCnt + "]");

        int reportChangeCnt = reportRepository.updateReportStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMenti...updateReportStatus..." + reportChangeCnt + "]");

        int reservationChangeCnt = reservationRepository.updateReservationStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMenti...updateReservationStatus..." + reservationChangeCnt + "]");

        int paymentChangeCnt = paymentRepository.updatePaymentStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
        log.info("[MemberServiceImpl.expelForMenti...updatePaymentStatus..." + paymentChangeCnt + "]");

    }
}
