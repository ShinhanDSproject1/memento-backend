package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.LoginRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

   final MemberRepository memberRepo;
   final PasswordEncoder pwEncoder;

    @Override
    public Member login(MemberType pathType, LoginRequest req) {
        final String id = req.getMemberId();
        final String rawPwd = req.getMemberPwd();

        // 1) ADMIN(관리자): 어느 탭(멘토/멘티)으로 들어와도 허용
        Optional<Member> adminOpt = memberRepo.findByMemberIdAndMemberType(id, MemberType.ADMIN); // 아이디+ADMIN 조회
        if (adminOpt.isPresent()) {
            Member admin = adminOpt.get(); //Member엔티티가서 관리자인지 확인
            if (!pwEncoder.matches(rawPwd, admin.getMemberPwd())) { //비번 확인
                log.warn("로그인 실패: 비밀번호가 틀렸습니다. (id={}, type=ADMIN)", id);
                throw new AuthException(BaseExceptionResponseStatus.INVALID_PASSWORD);
            }
            log.info("로그인 성공: (id={}, type=ADMIN)", id);
            return admin;
        }

        // 2) 선택한 타입(MENTO/MENTI)으로 조회
        Optional<Member> userOpt = memberRepo.findByMemberIdAndMemberType(id, pathType); // 아이디+선택한 멤버타입 조회
        if (!userOpt.isPresent()) { //해당 타입계정이 없을경우
            // 다른 타입 존재 여부로 '멤버타입 틀림' vs '아이디 틀림' 구분
            MemberType otherType = (pathType == MemberType.MENTI) ? MemberType.MENTO : MemberType.MENTI; // 반대 타입 계산
            Optional<Member> otherOpt = memberRepo.findByMemberIdAndMemberType(id, otherType); // 아이디+반대 멤버타입 조회

            if (otherOpt.isPresent()) { // 반대 멤버타입이 있을 경우
                log.warn("로그인 실패: 타입이 틀렸습니다. (id={}, 선택한 타입={})", id, pathType);
                throw new AuthException(BaseExceptionResponseStatus.CANNOT_LOGIN);
            } else {// 반대 멤버타입이 없을 경우
                log.warn("로그인 실패: 아이디가 틀렸습니다. (id={})", id);
                throw new AuthException(BaseExceptionResponseStatus.INVALID_MEMBER_ID);
            }
        }

        // 3) 비밀번호 검증(선택한 멤버타입과 아이디가 일치 할때)
        Member user = userOpt.get();
        if (!pwEncoder.matches(rawPwd, user.getMemberPwd())) {
            log.warn("로그인 실패: 비밀번호가 틀렸습니다. (id={}, type={})", id, user.getMemberType());
            throw new AuthException(BaseExceptionResponseStatus.INVALID_PASSWORD);
        }

        log.info("로그인 성공: (id={}, type={})", id, user.getMemberType());
        return user;
    }
}
