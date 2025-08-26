package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;

import java.util.List;

public interface MemberCustomRepository {
    List<Member> findAllByIdAndLimitAndCursor(Integer limit, Long cursor, BaseStatus status);
}
