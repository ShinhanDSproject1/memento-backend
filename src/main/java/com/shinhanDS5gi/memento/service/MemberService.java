package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.LoginRequest;

public interface MemberService {
    //아이디와 타입보고 로그인
    Member login(MemberType pathType, LoginRequest request);
}
