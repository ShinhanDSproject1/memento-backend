package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository{
    List<Member> findAllByIdAndLimitAndCursor(Integer limit, Long cursor, BaseStatus status);
}
