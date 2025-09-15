package com.shinhanDS5gi.memento.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhanDS5gi.memento.common.response.BaseResponse;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final StringRedisTemplate redisTemplate;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {

        log.info("Authorization Header from request: {}", req.getHeader("Authorization"));

        String token = readAccessToken(req);

        if (token == null) {
            fc.doFilter(req, res);
            return;
        }

        try {
            // claims()를 한 번만 호출
            Claims claims = jwtTokenUtil.claims(token);

            // 반환된 claims 객체에서 직접 값을 꺼내 사용
            String jti = claims.getId();
            String username = claims.getSubject();

            String black = redisTemplate.opsForValue().get(jwtTokenUtil.atblkKey(jti));
            if (black != null) {
                throw new JwtException("로그아웃된 토큰입니다.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);

            fc.doFilter(req, res);

        } catch (UsernameNotFoundException e) {
            // 토큰은 유효하나 DB에 유저 정보가 없는 경우
            log.warn("User not found from token: {}", e.getMessage());
            setErrorResponse(res, BaseExceptionResponseStatus.CANNOT_FOUND_MEMBER);
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 모든 경우 (만료, 형식 오류, 로그아웃된 토큰 등)
            log.warn("Invalid JWT: {}", e.getMessage());
            setErrorResponse(res, BaseExceptionResponseStatus.INVALID_TOKEN);
        }
    }

    private String readAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /* BaseExceptionResponseStatus를 사용하여 일관된 에러 응답 생성 */
    private void setErrorResponse(HttpServletResponse response, BaseExceptionResponseStatus status) throws IOException {
        response.setStatus(status.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // BaseResponse 또는 직접 Map을 생성하여 JSON 응답 포맷을 맞춤
        BaseResponse<Void> baseResponse = new BaseResponse<>(status, null);
        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
    }
}