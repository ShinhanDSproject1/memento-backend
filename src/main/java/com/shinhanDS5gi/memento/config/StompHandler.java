package com.shinhanDS5gi.memento.config;

import com.shinhanDS5gi.memento.security.JwtTokenUtil;
import com.shinhanDS5gi.memento.service.UserDetailsServiceImpl;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.security.UserAdapter;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("STOMP Authorization Header: {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                try {
                    if (!jwtTokenUtil.isExpired(token)) {
                        String username = jwtTokenUtil.getSubject(token);

                        // UserDetails가 아닌 Member 객체를 직접 가져옴
                        Member member = userDetailsService.findMemberByLoginId(username);

                        // Member 객체로 UserAdapter 인스턴스를 생성
                        UserAdapter userAdapter = new UserAdapter(member);

                        // UserAdapter를 사용하여 인증 객체를 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.getAuthorities());

                        // setUser 대신 세션 속성에 직접 저장합니다. 이것이 더 확실합니다.
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            sessionAttributes.put("user", authentication);
                        }

                        // setUser도 호출은 유지합니다.
                        accessor.setUser(authentication);
                        log.info("STOMP connection authenticated for user: {}", username);
                    }
                } catch (JwtException | IllegalArgumentException e) {
                    log.warn("STOMP connection failed: Invalid JWT token - {}", e.getMessage());
                }
            } else {
                log.warn("STOMP connection failed: No JWT token found in headers");
            }
        }
        return message;
    }
}