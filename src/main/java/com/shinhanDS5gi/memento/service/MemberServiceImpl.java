package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.dto.admin.GetMemberListResponse;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public GetMemberListResponse getMemberList(Integer limit, Long cursor) {
        log.info("[MemberServiceImpl.getMemberList]");

        List<Member> memberList = memberRepository.findAllByIdAndLimitAndCursor(limit, cursor, BaseStatus.ACTIVE);

        List<GetMemberListResponse.MemberInfo> memberInfoList = memberList.stream().map(member->new GetMemberListResponse.MemberInfo(
                member.getMemberSeq(), member.getMemberName(), member.getMemberType().toString(), member.getCreatedAt().toLocalDate()
        )).limit(limit).toList();

        GetMemberListResponse result;
        if(memberList.size()<=limit) {
            result = GetMemberListResponse.builder().members(memberInfoList).hasNext(false).build();
        }else{
            result = GetMemberListResponse.builder().members(memberInfoList).hasNext(true).build();
        }
        return result;
    }
}
