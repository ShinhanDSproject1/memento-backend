package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
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

