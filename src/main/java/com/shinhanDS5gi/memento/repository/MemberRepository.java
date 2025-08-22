package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}