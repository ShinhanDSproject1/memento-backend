package com.shinhanDS5gi.memento.security;

import com.shinhanDS5gi.memento.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final StringRedisTemplate RedisTemplate;

    //AT 토큰 찾아서 읽기
    private String readAccessToken(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("AT".equals(c.getName())) return c.getValue();
            }
        }
        //쿠키가 없으면 HTTP 헤더 Authorization 확인
        String h = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (h != null && h.startsWith("Bearer ")) return h.substring(7);
        return null;
    }

    //매 요청마다 JWT 인증을 수행하고, 유효하면 SecurityContext에 인증 객체 넣음-> 로그인 후 사용가능하게 함
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String uri = req.getServletPath();
        if (uri.startsWith("/auth/login") || uri.startsWith("/auth/refresh") || uri.startsWith("/auth/logout")) {
            chain.doFilter(req, res);
            return;
        }
        //토큰 검증 + 인증 세팅
        try {
            String h = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (h != null && h.startsWith("Bearer ")) {
                String at = h.substring(7);
                if (jwtTokenUtil.validate(at)) {
                    // 블랙리스트 체크
                    String jti = jwtTokenUtil.getJti(at);
                    String blocked = RedisTemplate.opsForValue().get(jwtTokenUtil.atblkKey(jti));
                    if (blocked == null) {
                        var auth = new UsernamePasswordAuthenticationToken(
                                jwtTokenUtil.getSubject(at), null, List.of()
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception e) {
            // at 확인 불가 SecurityContext 초기화
            SecurityContextHolder.clearContext();
            log.debug("JWT filter error", e);
        }
        chain.doFilter(req, res);
    }
}