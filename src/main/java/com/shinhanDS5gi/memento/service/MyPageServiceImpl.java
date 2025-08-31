package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.MyProfileResponse;
import com.shinhanDS5gi.memento.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    
    /* 나의 프로필 정보 조회 */
    @Override
    public MyProfileResponse getMyProfile(Long memberSeq) {

        Member member = memberRepository.findByMemberSeqAndStatus(memberSeq, BaseStatus.ACTIVE)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

        return MyProfileResponse.builder()
                .memberName(member.getMemberName())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberBirthDate(member.getMemberBirthDate())
                .memberId(member.getMemberId())
                .build();
    }
}
