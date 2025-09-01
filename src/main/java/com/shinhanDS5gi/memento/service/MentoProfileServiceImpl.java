package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MentoProfileException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.MentoProfile;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.CreateMentoProfileRequest;
import com.shinhanDS5gi.memento.dto.UpdateMentoProfileRequest;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import com.shinhanDS5gi.memento.repository.MentoProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoProfileServiceImpl implements MentoProfileService {

    private final MentoProfileRepository mentoProfileRepository;
    private final MemberRepository memberRepository;

    /* 멘토 프로필 생성 */
    @Override
    @Transactional
    public void createMentoProfile(Long memberSeq, CreateMentoProfileRequest requestDto) {

        /* 회원 가입한 사용자인가? */
        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        /* 멘토인가? */
        if (member.getMemberType() != MemberType.MENTO) {
            throw new MentoProfileException(FAILURE, "멘토 회원만 프로필을 생성할 수 있습니다.");
        }

        /* 이미 멘토프로필 생성하진 않았는가? */
        if (mentoProfileRepository.existsByMember_MemberSeq(memberSeq)) {
            throw new MentoProfileException(ALREADY_EXISTS_MENTO_PROFILE);
        }

        MentoProfile mentoProfile = requestDto.toEntity(member);
        mentoProfileRepository.save(mentoProfile);
    }

    /* 멘토 프로필 수정 */
    @Override
    @Transactional
    public void updateMentoProfile(Long memberSeq, UpdateMentoProfileRequest requestDto) {

        /* 회원 가입한 사용자인가? */
        memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MentoProfileException(CANNOT_FOUND_MEMBER));

        /* 수정할 프로필을 조회 */
        MentoProfile mentoProfile = mentoProfileRepository.findByMember_MemberSeq(memberSeq)
                .orElseThrow(() -> new MentoProfileException(CANNOT_FOUND_MENTO_PROFILE));

        mentoProfile.update(requestDto);
    }
}
