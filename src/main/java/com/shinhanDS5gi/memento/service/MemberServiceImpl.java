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
import com.shinhanDS5gi.memento.repository.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentoCertificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.DirectFieldAccessor;
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
    /**
     * 회원탈퇴
     */
    @Override
    public void withdraw(Long memberSeq) { // 회원 PK로 탈퇴 수행
        // 1) ACTIVE인 회원만 찾음 → 없거나 이미 INACTIVE면 바로 오류메세지
        Member member = memberRepo.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
        // 2) status를 INACTIVE로 변경
        new DirectFieldAccessor(member).setPropertyValue("status", BaseStatus.INACTIVE);
        // 종료 시 커밋 → UPDATE 자동 실행(더티체킹)
    }
    /**
     * 로그아웃
     */
    @Override
    public void logout(Long memberSeq) {
        //memberSeq를 가진 멤버를 member테이블에서 조회
        memberRepo.findById(memberSeq)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
        log.info("로그아웃 성공: memberSeq={}", memberSeq);
    }

    /**
     * 로그인 기능
     */
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


    /**
     * 멘토 회원가입
     */
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
        //자격증 없으면 스킵처리 / 있으면 저장
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

    /**
     * 멘티 회원가입
     */
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
}
