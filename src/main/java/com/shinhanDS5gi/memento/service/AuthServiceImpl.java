package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.dto.auth.AccessTokenResponse;
import com.shinhanDS5gi.memento.security.JwtTokenUtil;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.LoginRequest;
import com.shinhanDS5gi.memento.repository.AuthRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus.*;
import static com.shinhanDS5gi.memento.domain.base.BaseStatus.ACTIVE;

import java.time.Duration;
import java.util.Date;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final StringRedisTemplate RedisTemplate;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.accessTokenExpiration}")
    private long accessExpMs;
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshExpMs;

    private static final String AT = "AT";
    private static final String RT = "RT";


    /** 로그인 */
    @Override
    public Member login(MemberType pathType, LoginRequest req) {
        final String id = req.getMemberId();
        final String rawPwd = req.getMemberPwd();


        // 1) 아이디로 먼저 조회 → ADMIN이면 pathType 무시
        Member admin = authRepository.findByMemberId(id)
                .orElseThrow(() -> new AuthException(INVALID_MEMBER_ID));

        if (admin.getMemberType() == MemberType.ADMIN) {
            if (admin.getStatus() != ACTIVE) throw new AuthException(CANNOT_LOGIN);
            if (!passwordEncoder.matches(rawPwd, admin.getMemberPwd())) {
                log.info("로그인 실패: 비밀번호 틀림 (id={}, type=ADMIN)", id);
                throw new AuthException(INVALID_PASSWORD);
            }
            return admin;
        }

        // 2) 멘토/멘티 로그인: 해당 타입 + ACTIVE
        Member user = authRepository
                .findByMemberIdAndMemberTypeAndStatus(id, pathType, ACTIVE)
                .orElseThrow(() -> {
                    MemberType other = (pathType == MemberType.MENTI) ? MemberType.MENTO : MemberType.MENTI;
                    if (authRepository.findByMemberIdAndMemberTypeAndStatus(id, other, ACTIVE).isPresent()) {
                        //타입 불일치
                        return new AuthException(CANNOT_LOGIN);
                    }
                    if (authRepository.findByMemberId(id).isEmpty()) {
                        log.info("로그인 실패: 아이디 없음 (id={})", id);
                        return new AuthException(INVALID_MEMBER_ID);
                    }
                    // inactive
                    return new AuthException(CANNOT_LOGIN);
                });
        if (!passwordEncoder.matches(rawPwd, user.getMemberPwd())) {
            log.info("로그인 실패: 비밀번호 틀림 (id={}, type={})", id, user.getMemberType());
            throw new AuthException(INVALID_PASSWORD);
        }
        return user;
    }


    /** 토큰 발급 cookie에 저장*/
    @Override
    @Transactional
    public Map<String, Object> issueTokens(MemberType type, LoginRequest req) {
        Member m = login(type, req);
        String sub = m.getMemberId();
        //AT,RT 발급
        String at = jwtTokenUtil.createAccessToken(sub, 0, m.getMemberType().name());
        String rt = jwtTokenUtil.createRefreshToken(sub, 0);
        RedisTemplate.opsForValue().set(jwtTokenUtil.rtKey(sub), RT, Duration.ofMillis(refreshExpMs));
        return Map.of(
                "member", m,
                "accessToken", at,
                "refreshToken", rt
        );
    }

    /** 재발급 (AT 토큰이 만료 됨 ->AT 재발급해줌) */
    @Override
    @Transactional
    public AccessTokenResponse refresh(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        String rt = jwtTokenUtil.readCookie(req, RT);
        if (rt == null) {
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }
        try {
            String sub = jwtTokenUtil.getSubject(rt);
            // Redis에서 RT 검증
            String saved = RedisTemplate.opsForValue().get(jwtTokenUtil.rtKey(sub));
            if (saved == null || !RT.equals(saved)) {
                throw new AuthException(INVALID_REFRESH_TOKEN);
            }
            // DB에서 멤버 타입 조회 및 새 AT 발급
            Member m = authRepository.findByMemberId(sub)
                    .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));

            return new AccessTokenResponse(
                    jwtTokenUtil.createAccessToken(sub, 0, m.getMemberType().name()));

        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 파싱/검증 실패 시 (만료, 형식 오류 등) 예외 처리
            log.warn("Invalid Refresh Token: {}", e.getMessage());
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }
    }

    /** 로그아웃*/
    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        cleanupTokensAndCookies(req, res, secureCookie);
    }

    /** RT제거 AT블랙리스트 */
    @Override
    public void cleanupTokensAndCookies(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        // AT 쿠키 처리
        String at = jwtTokenUtil.readCookie(req, AT);
        if (at != null) {
            try {
                // claims()를 한 번만 호출
                Claims claims = jwtTokenUtil.claims(at);
                Date expiration = claims.getExpiration();
                long ttl = expiration.getTime() - System.currentTimeMillis();

                if (ttl > 0) {
                    // claims 객체에서 jti(id)를 직접 가져옴
                    String jti = claims.getId();
                    RedisTemplate.opsForValue().set(jwtTokenUtil.atblkKey(jti), "1", Duration.ofMillis(ttl));
                }
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("Invalid Access Token during logout: {}", e.getMessage());
            }
        }

        // RT 쿠키 처리
        String rt = jwtTokenUtil.readCookie(req, RT);
        if (rt != null) {
            try {
                // 토큰이 유효한 경우에만 Redis에서 RT 삭제
                String sub = jwtTokenUtil.getSubject(rt);
                RedisTemplate.delete(jwtTokenUtil.rtKey(sub));
            } catch (JwtException | IllegalArgumentException e) {
                // 유효하지 않은 RT는 무시하고 다음 로직으로 넘어감
                log.warn("Invalid Refresh Token during logout: {}", e.getMessage());
            }
        }

        // 브라우저에서 AT/RT 쿠키 제거 (토큰 유효성과 상관없이 항상 실행)
        jwtTokenUtil.clearCookie(res, AT, secureCookie);
        jwtTokenUtil.clearCookie(res, RT, secureCookie);
    }

}
