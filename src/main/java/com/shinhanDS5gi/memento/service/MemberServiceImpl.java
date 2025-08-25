package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.LoginRequest;
import com.shinhanDS5gi.memento.dto.MentoCertificationRequest;
import com.shinhanDS5gi.memento.dto.MentoSignupRequest;
import com.shinhanDS5gi.memento.dto.MentiSignupRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentoCertificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepo;
    private final PasswordEncoder pwEncoder;
    private final MentoCertificationRepository certRepo;

    /**
     * 로그인 기능
     */
    @Override
    @Transactional(readOnly = true)
    public Member login(MemberType pathType, LoginRequest req) {
        final String id = req.getMemberId();
        final String rawPwd = req.getMemberPwd();

        // 1) ADMIN 로그인 처리
        Optional<Member> adminOpt = memberRepo.findByMemberIdAndMemberType(id, MemberType.ADMIN);
        if (adminOpt.isPresent()) {
            Member admin = adminOpt.get();
            if (!pwEncoder.matches(rawPwd, admin.getMemberPwd())) {
                log.warn("로그인 실패: 비밀번호 틀림 (id={}, type=ADMIN)", id);
                throw new AuthException(BaseExceptionResponseStatus.INVALID_PASSWORD);
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
                throw new AuthException(BaseExceptionResponseStatus.CANNOT_LOGIN);
            } else {
                log.warn("로그인 실패: 아이디 불일치 (id={})", id);
                throw new AuthException(BaseExceptionResponseStatus.INVALID_MEMBER_ID);
            }
        }

        // 3) 비밀번호 검증
        Member user = userOpt.get();
        if (!pwEncoder.matches(rawPwd, user.getMemberPwd())) {
            log.warn("로그인 실패: 비밀번호 틀림 (id={}, type={})", id, user.getMemberType());
            throw new AuthException(BaseExceptionResponseStatus.INVALID_PASSWORD);
        }

        log.info("로그인 성공: (id={}, type={})", id, user.getMemberType());
        return user;
    }

    /**
     * 멘토 회원가입
     */  
    @Transactional(readOnly = true) // 이 클래스 기본은 읽기 전용
    public class MemberServiceImpl implements MemberService {

        private final MemberRepository memberRepo;

        @Override
        public void logout(Long memberSeq) {
            memberRepo.findById(memberSeq) //memberSeq를 가진 멤버를 member테이블에서 조회
                    .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
            log.info("로그아웃 성공: memberSeq={}", memberSeq);
        }
    }

    /**
     * 멘토 회원가입
     */
    @Override
    public void signupMento(MentoSignupRequest req) {
        if (memberRepo.existsByMemberId(req.getMemberId())) {
            throw new MemberException(BaseExceptionResponseStatus.CANNOT_SIGNUP);
        }

        LocalDate birth = LocalDate.parse(req.getMemberBirthDate());

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

        List<MentoCertificationRequest> certReqs = req.getCertification();
        if (certReqs != null && !certReqs.isEmpty()) {
            List<MentoCertification> entities = new ArrayList<>(certReqs.size());
            for (MentoCertificationRequest c : certReqs) {
                entities.add(new MentoCertification(
                        null,
                        c.getCertificationName(),
                        c.getCertificationFile(),
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
        if (memberRepo.existsByMemberId(req.getMemberId())) {
            throw new MemberException(BaseExceptionResponseStatus.CANNOT_SIGNUP);
        }

        LocalDate birth = LocalDate.parse(req.getMemberBirthDate());

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
