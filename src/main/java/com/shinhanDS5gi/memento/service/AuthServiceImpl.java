package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.security.JwtTokenUtil;
import com.shinhanDS5gi.memento.domain.member.Member;
import com.shinhanDS5gi.memento.domain.member.MemberType;
import com.shinhanDS5gi.memento.dto.auth.LoginRequest;
import com.shinhanDS5gi.memento.repository.AuthRepository;
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

        // 1) ADMIN 로그인은 ADMIN + ACTIVE
        if (pathType == MemberType.ADMIN) {
            Member admin = authRepository
                    .findByMemberIdAndMemberTypeAndStatus(id, MemberType.ADMIN, ACTIVE)
                    .orElseThrow(() -> {
                        // 아이디 자체가 없으면 INVALID_MEMBER_ID, 있으면 타입/상태 문제
                        return authRepository.findByMemberId(id).isEmpty()
                                ? new AuthException(INVALID_MEMBER_ID)
                                : new AuthException(CANNOT_LOGIN);
                    });
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

    public void refresh(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        //cookie값 RT 검증
        String rt = jwtTokenUtil.readCookie(req, RT);
        if (rt == null || !jwtTokenUtil.validate(rt)) {
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }
        //Redis에서 RT 검증
        String sub = jwtTokenUtil.getSubject(rt);
        String saved = RedisTemplate.opsForValue().get(jwtTokenUtil.rtKey(sub));
        if (saved == null || !saved.equals(rt)) {
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }
        // DB에서 멤버 타입 조회
        Member m = authRepository.findByMemberId(sub)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
        //새 AT 토큰 발급 + 저장/세팅
        String at = jwtTokenUtil.createAccessToken(sub, 0, m.getMemberType().name());

        jwtTokenUtil.setCookie(res, AT, at, Duration.ofMillis(accessExpMs),  secureCookie);

    }

    /** 로그아웃*/
    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        cleanupTokensAndCookies(req, res, secureCookie);
    }

    /** RT제거 AT블랙리스트 */
    @Override
    public void cleanupTokensAndCookies(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        //AT 쿠키가 있고 유효하면 남은 시간만큼 블랙리스트 키등록
        String at = jwtTokenUtil.readCookie(req, AT);
        if (at != null && jwtTokenUtil.validate(at)) {
            long ttl = jwtTokenUtil.remainingMs(at);
            if (ttl > 0) {
                RedisTemplate.opsForValue().set(jwtTokenUtil.atblkKey(jwtTokenUtil.getJti(at)), "1", Duration.ofMillis(ttl));
            }
        }
        //RT 쿠키가 있고 유효하면 Redis의 RT 저장값 삭제
        String rt = jwtTokenUtil.readCookie(req, RT);
        if (rt != null && jwtTokenUtil.validate(rt)) {
            String sub = jwtTokenUtil.getSubject(rt);
            RedisTemplate.delete(jwtTokenUtil.rtKey(sub)); // 재발급 차단
        }
        //브라우저에서 AT/RT 쿠키 제거
        jwtTokenUtil.clearCookie(res, AT, secureCookie);
        jwtTokenUtil.clearCookie(res, RT, secureCookie);
    }


}
