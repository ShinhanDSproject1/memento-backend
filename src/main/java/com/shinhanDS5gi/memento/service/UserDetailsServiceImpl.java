package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.repository.AuthRepository;
import com.shinhanDS5gi.memento.security.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = authRepository.findByMemberId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
        return new UserAdapter(member);
    }

    public Member findMemberByLoginId(String loginId) {
        return authRepository.findByMemberId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
    }
}
