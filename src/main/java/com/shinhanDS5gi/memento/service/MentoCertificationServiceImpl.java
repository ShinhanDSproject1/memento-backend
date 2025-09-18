package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.common.exception.MentoCertificationException;
import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.mento.MentoCertificationsResponse;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.mento.MentoCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoCertificationServiceImpl implements MentoCertificationService {

    private final MemberRepository memberRepository;
    private final MentoCertificationRepository mentoCertificationRepository;
    private final IdempotencyService idempotencyService;

    /* 내 보유 자격증 목록 조회 */
    @Override
    public List<MentoCertificationsResponse> getMentoCertifications(Long memberSeq) {

        memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        List<MentoCertification> certifications = mentoCertificationRepository.findAllByMember_MemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE);

        return certifications.stream()
                .map(MentoCertificationsResponse::from)
                .collect(Collectors.toList());
    }

    /* 내 자격증 추가 */
    @Override
    @Transactional
    public void createMentoCertification(Long memberSeq, String certificationName, String certificationImgUrl, String idemKey) {

        if (idempotencyService.isDuplicate(idemKey)) {
            throw new MentoCertificationException(ALREADY_SUCCESS_REQUEST);
        }

        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        if (member.getMemberType() != MemberType.MENTO) {
            throw new MemberException(NOT_A_MENTO);
        }

        // null Check
        if (certificationImgUrl == null || certificationImgUrl.isEmpty()) {
            throw new IllegalArgumentException("자격증 이미지 파일은 필수입니다.");
        }


        MentoCertification certification = new MentoCertification(
                null,
                certificationName,
                certificationImgUrl,
                BaseStatus.ACTIVE,
                member
        );
        MentoCertification savedCertification = mentoCertificationRepository.save(certification);

        // 멱등키 저장
        idempotencyService.saveKey(idemKey, String.valueOf(savedCertification.getMentoCertificationSeq()));
    }
}