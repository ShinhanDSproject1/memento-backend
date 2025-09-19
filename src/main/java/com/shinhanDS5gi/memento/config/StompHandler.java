package com.shinhanDS5gi.memento.config;

import com.shinhanDS5gi.memento.security.JwtTokenUtil;
import com.shinhanDS5gi.memento.service.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
/* 채팅 관련 서비스 시 인증 정보를 전달하기 위한 Handler */
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
                    // 토큰 유효성 검사 (만료, 형식 등)
                    if (!jwtTokenUtil.isExpired(token)) {
                        // 토큰에서 사용자 정보(username) 추출
                        String username = jwtTokenUtil.getSubject(token);

                        // UserDetails 객체 로드
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 인증 객체 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // STOMP 세션에 인증 정보 저장
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