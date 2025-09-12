package com.shinhanDS5gi.memento.security;

import com.shinhanDS5gi.memento.domain.member.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAdapter extends User {

    private final Member member;

    public UserAdapter(Member member) {
        // UserDetails의 생성자를 호출하여 username, password, authorities를 설정
        // username은 memberId를, password는 실제 비밀번호를, 권한은 memberType을 사용
        super(member.getMemberId(), member.getMemberPwd(), List.of(new SimpleGrantedAuthority("ROLE_" + member.getMemberType().name())));
        this.member = member;
    }
}