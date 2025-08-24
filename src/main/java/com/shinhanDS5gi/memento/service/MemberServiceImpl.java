package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.MentoCertificationRequest;
import com.shinhanDS5gi.memento.dto.MentoSignupRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentoCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    final MemberRepository memberRepo;
    final PasswordEncoder passwordEncoder;
    private final MentoCertificationRepository certRepo;

        @Override
        public void signupMento(MentoSignupRequest req) {
            // 1) 중복 아이디 체크
            if (memberRepo.existsByMemberId(req.getMemberId())) {
                throw new MemberException(BaseExceptionResponseStatus.CANNOT_SIGNUP);
            }
            // 2) 생년월일 파싱 (yyyy-MM-dd)
            LocalDate birth = LocalDate.parse(req.getMemberBirthDate());

            // 3) 엔티티에 회원 저장 (MENTO)
            Member member = Member.builder()
                    .memberId(req.getMemberId())
                    .memberPwd(passwordEncoder.encode(req.getMemberPwd()))
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
                            null,     // PK 자동
                            c.getCertificationName(),  // mentoCertificationName
                            c.getCertificationFile(),  // mentoCertificationImage
                            BaseStatus.ACTIVE,
                            member
                    ));
                }
                certRepo.saveAll(entities);
            }
        }

}
