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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final StringRedisTemplate redisTemplate; // 변수명 컨벤션 수정 (RedisTemplate -> redisTemplate)
    private final UserDetailsServiceImpl userDetailsService;

    // AT 토큰 찾아서 읽기 (Bearer 토큰 우선)
    private String readAccessToken(HttpServletRequest req) {
        // Postman 등 외부 도구를 위해 HTTP 헤더의 Authorization을 먼저 확인
        String h = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (h != null && h.startsWith("Bearer ")) {
            return h.substring(7);
        }

        // 헤더에 토큰이 없으면, 웹 브라우저를 위해 쿠키를 확인
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("AT".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {
        try {
            // 1. Bearer 토큰 또는 쿠키에서 Access Token을 읽어옴
            String token = readAccessToken(req);

            // 2. 토큰이 유효한지 검증
            if (token != null && jwtTokenUtil.validate(token)) {
                // 3. 로그아웃(블랙리스트)된 토큰인지 확인
                String jti = jwtTokenUtil.getJti(token);
                String black = redisTemplate.opsForValue().get(jwtTokenUtil.atblkKey(jti));

                if (black == null) {
                    // 4. UserDetailsServiceImpl을 통해 Member 정보가 담긴 UserDetails 객체를 가져옴
                    String username = jwtTokenUtil.getSubject(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 5. 인증 객체를 생성하여 SecurityContext에 저장 (@CurrentUser가 작동하도록 함)
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception ex) {
            log.warn("JWT filter error: {}", ex.toString());
            SecurityContextHolder.clearContext();
        }

        // 다음 필터로 요청을 전달
        fc.doFilter(req, res);
    }
}
