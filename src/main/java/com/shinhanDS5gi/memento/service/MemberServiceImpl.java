package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.MentiSignupRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    final MemberRepository memberRepo;
    final PasswordEncoder passwordEncoder;


    @Override
    public void signupMenti(MentiSignupRequest req) {
        // 1) 중복 아이디 체크
        if (memberRepo.existsByMemberId(req.getMemberId())) {
            throw new MemberException(BaseExceptionResponseStatus.CANNOT_SINGUP);
        }
        // 2) 생년월일 파싱 (yyyy-MM-dd)
        LocalDate birth = LocalDate.parse(req.getMemberBirthDate());
        // 3) 저장
        Member m = Member.builder()
                .memberId(req.getMemberId())
                .memberPwd(passwordEncoder.encode(req.getMemberPwd()))
                .memberName(req.getMemberName())
                .memberPhoneNumber(req.getMemberPhoneNumber())
                .memberBirthDate(birth)
                .memberType(MemberType.MENTI)
                .status(BaseStatus.ACTIVE)
                .build();

        memberRepo.save(m);
    }
}
