package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.MentoCertification;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.CreateMentoCertificationRequest;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentoCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.NOT_A_MENTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoCertificationServiceImpl implements MentoCertificationService {

    private final MemberRepository memberRepository;
    private final MentoCertificationRepository mentoCertificationRepository;

    @Override
    @Transactional
    public void createMentoCertification(Long memberSeq, CreateMentoCertificationRequest requestDto) {
        // 1. 사용자 조회
        Member member = memberRepository.findById(memberSeq)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        // 2. 멘토 타입인지 검증
        if (member.getMemberType() != MemberType.MENTO) {
            throw new MemberException(NOT_A_MENTO);
        }

        // 3. DTO 리스트를 엔티티 리스트로 변환
        List<MentoCertification> certifications = requestDto.getCertifications().stream()
                .map(certInfo -> certInfo.toEntity(member))
                .collect(Collectors.toList());

        mentoCertificationRepository.saveAll(certifications);
    }
}
