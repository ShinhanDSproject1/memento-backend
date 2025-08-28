package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.LoginRequest;
import com.shinhanDS5gi.memento.dto.auth.MentoCertificationRequest;
import com.shinhanDS5gi.memento.dto.auth.MentoSignupRequest;
import com.shinhanDS5gi.memento.dto.auth.MentiSignupRequest;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.repository.MentoCertificationRepository;
import com.shinhanDS5gi.memento.repository.*;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import org.springframework.beans.DirectFieldAccessor;

import com.shinhanDS5gi.memento.repository.mentos.MentosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepo;
    private final PasswordEncoder pwEncoder;
    private final MentoCertificationRepository certRepo;
    private final MentosRepository mentosRepository;
    private final MentoProfileRepository mentoProfileRepository;
    private final ReviewRepository reviewRepository;
    private final ReportRepository reportRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    /* 관리자 페이지 전체 회원 조회하기 */
    @Override
    public GetMemberListResponse getMemberList(Integer limit, Long cursor) {
        log.info("[MemberServiceImpl.getMemberList]");

        List<Member> memberList = memberRepo.findAllByIdAndLimitAndCursor(limit, cursor, BaseStatus.ACTIVE);

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
    public void withdraw(Long memberSeq) { // 회원 PK로 탈퇴 수행
        //ACTIVE인 회원만 찾음 → 없거나 이미 INACTIVE면 바로 오류메세지
        log.info("[MemberServiceImpl.expelMemberByAdmin]");
        Member member = memberRepo.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        // member 테이블 무효화 (영속성 컨텍스트 / 1차 캐시를 이용하려고 따로 save 메소드 호출하지 않음)
        member.updateMemberStatus(BaseStatus.INACTIVE);

        if (member.getMemberType().equals(MemberType.MENTO)) {
            expelForMento(member.getMemberSeq());
        } else if (member.getMemberType().equals(MemberType.MENTI)) {
            expelForMenti(member.getMemberSeq());
        }


    }

    /* 로그아웃 */
    @Override
    public void logout(Long memberSeq) {
        memberRepo.findById(memberSeq)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
        log.info("로그아웃 성공: memberSeq={}", memberSeq);
    }



    /* 로그인 */
    @Override
    public Member login(MemberType pathType, LoginRequest req) {
        final String id = req.getMemberId();
        final String rawPwd = req.getMemberPwd();

        // 1) ADMIN 로그인 처리
        Optional<Member> adminOpt = memberRepo.findByMemberIdAndMemberType(id, MemberType.ADMIN);
        if (adminOpt.isPresent()) {
            Member admin = adminOpt.get();
            if (!pwEncoder.matches(rawPwd, admin.getMemberPwd())) {
                log.warn("로그인 실패: 비밀번호 틀림 (id={}, type=ADMIN)", id);
                throw new AuthException(INVALID_PASSWORD);
            }
            log.info("로그인 성공: (id={}, type=ADMIN)", id);
            return admin;
        }
        // 2) 선택한 타입(MENTO/MENTI) 로그인 처리
        Optional<Member> userOpt = memberRepo.findByMemberIdAndMemberType(id, pathType);
        if (!userOpt.isPresent()) {
            // 다른 타입 여부 확인
            MemberType otherType = (pathType == MemberType.MENTI) ? MemberType.MENTO : MemberType.MENTI;
            Optional<Member> otherOpt = memberRepo.findByMemberIdAndMemberType(id, otherType);
            if (otherOpt.isPresent()) {
                log.warn("로그인 실패: 타입 불일치 (id={}, 선택한 타입={})", id, pathType);
                throw new AuthException(CANNOT_LOGIN);
            } else {
                log.warn("로그인 실패: 아이디 불일치 (id={})", id);
                throw new AuthException(INVALID_MEMBER_ID);
            }
        }
        // 3) 비밀번호 검증
        Member user = userOpt.get();
        if (!pwEncoder.matches(rawPwd, user.getMemberPwd())) {
            log.warn("로그인 실패: 비밀번호 틀림 (id={}, type={})", id, user.getMemberType());
            throw new AuthException(INVALID_PASSWORD);
        }
        log.info("로그인 성공: (id={}, type={})", id, user.getMemberType());
        return user;
    }




    /* 멘토 회원가입 */
    @Override
    public void signupMento(MentoSignupRequest req) {
        // 1) 중복 아이디 체크
        if (memberRepo.existsByMemberId(req.getMemberId())) {
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
        memberRepo.save(member);
        // 4) 자격증 저장
        List<MentoCertificationRequest> certReqs = req.getCertification();
        if (certReqs != null && !certReqs.isEmpty()) {
            //DTO를 엔티티로 변환해서 List에 저장
            List<MentoCertification> entities = new ArrayList<>(certReqs.size());
            //자격증 하나씩 처리
            for (MentoCertificationRequest c : certReqs) {
                entities.add(new MentoCertification(
                        null, // PK 자동
                        c.getCertificationName(), // mentoCertificationName
                        c.getCertificationFile(), // mentoCertificationImage
                        BaseStatus.ACTIVE,
                        member
                ));
            }
            certRepo.saveAll(entities);
        }
    }



    /* 멘티 회원가입 */
    @Override
    public void signupMenti(MentiSignupRequest req) {
        // 1) 중복 아이디 체크
        if (memberRepo.existsByMemberId(req.getMemberId())) {
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

        memberRepo.save(m);
    }



    /* 회원 제명하기 (관리자) */
    @Transactional
    @Override
    public void expelMemberByAdmin(Long memberSeq) {
        log.info("[MemberServiceImpl.expelMemberByAdmin]");
        Member member = memberRepo.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE).orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
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

        int certificationChangeCnt = certRepo.updateMentoCertificationStatus(memberSeq, BaseStatus.INACTIVE, BaseStatus.ACTIVE);
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
