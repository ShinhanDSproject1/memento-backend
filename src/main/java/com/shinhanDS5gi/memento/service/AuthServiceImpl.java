package com.shinhanDS5gi.memento.service;

import com.shinhanDS5gi.memento.common.exception.AuthException;
import com.shinhanDS5gi.memento.common.exception.MemberException;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
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
import java.time.Duration;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final StringRedisTemplate RedisTemplate;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.accessTokenExpiration}")  private long accessExpMs;
    @Value("${jwt.refreshTokenExpiration}") private long refreshExpMs;

    private static final String AT = "AT";
    private static final String RT = "RT";


    /** 로그인 */
    @Override
    public Member login(MemberType pathType, LoginRequest req) {
        final String id = req.getMemberId();
        final String rawPwd = req.getMemberPwd();

        // ADMIN인지 확인
        Optional<Member> adminOpt = authRepository.findByMemberIdAndMemberType(id, MemberType.ADMIN);
        if (adminOpt.isPresent()) {
            Member admin = adminOpt.get();
            if (!passwordEncoder.matches(rawPwd, admin.getMemberPwd())) {
                log.warn("로그인 실패: 비밀번호 틀림 (id={}, type=ADMIN)", id);
                throw new AuthException(INVALID_PASSWORD);
            }
            return admin;
        }

        // 멘토/멘티 ACTIVE만 허용
        Optional<Member> userOpt =
                authRepository.findByMemberIdAndMemberTypeAndStatus(id, pathType, BaseStatus.ACTIVE);
        if (userOpt.isEmpty()) {
            // 다른 타입인지 확인
            MemberType otherType = (pathType == MemberType.MENTI) ? MemberType.MENTO : MemberType.MENTI;
            if (authRepository.findByMemberIdAndMemberType(id, otherType).isPresent()) {
                log.warn("로그인 실패: 타입 불일치 (id={}, selected={})", id, pathType);
                throw new AuthException(CANNOT_LOGIN);
            } else {
                log.warn("로그인 실패: 아이디 없음 (id={})", id);
                throw new AuthException(INVALID_MEMBER_ID);
            }
        }

        Member user = userOpt.get();
        if (!passwordEncoder.matches(rawPwd, user.getMemberPwd())) {
            log.warn("로그인 실패: 비밀번호 틀림 (id={}, type={})", id, user.getMemberType());
            throw new AuthException(INVALID_PASSWORD);
        }
        return user;
    }


    /** 토큰 발급 cookie에 저장*/
    @Override
    @Transactional
    public Member issueTokens(MemberType type, LoginRequest req,
                              HttpServletResponse res, boolean secureCookie) {
        Member m = login(type, req);  // 비번 검증 포함
        String sub = m.getMemberId();
        //AT,RT 발급
        String at = jwtTokenUtil.createAccessToken(sub, 0, m.getMemberType().name());
        String rt = jwtTokenUtil.createRefreshToken(sub, 0);


        RedisTemplate.opsForValue().set(jwtTokenUtil.rtKey(sub), rt, Duration.ofMillis(refreshExpMs));

        jwtTokenUtil.setCookie(res, AT, at, Duration.ofMillis(accessExpMs),  secureCookie);
        jwtTokenUtil.setCookie(res, RT, rt, Duration.ofMillis(refreshExpMs), secureCookie);

        return m;
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
        //기존 AT 블랙리스트 등록
        String oldAt = jwtTokenUtil.readCookie(req, AT);
        if (oldAt != null && jwtTokenUtil.validate(oldAt)) {
            long ttl = jwtTokenUtil.remainingMs(oldAt);
            if (ttl > 0) {
                RedisTemplate.opsForValue().set(jwtTokenUtil.atblkKey(jwtTokenUtil.getJti(oldAt)), "1", Duration.ofMillis(ttl));
            }
        }
        // DB에서 멤버 타입 조회
        Member m = authRepository.findByMemberId(sub)
                .orElseThrow(() -> new MemberException(CANNOT_FOUND_MEMBER));
        //새 토큰 발급 + 저장/세팅
        String at = jwtTokenUtil.createAccessToken(sub, 0, m.getMemberType().name());
        rt = jwtTokenUtil.createRefreshToken(sub, 0);

        RedisTemplate.opsForValue().set(jwtTokenUtil.rtKey(sub), rt, Duration.ofMillis(refreshExpMs));

        jwtTokenUtil.setCookie(res, AT, at, Duration.ofMillis(accessExpMs),  secureCookie);
        jwtTokenUtil.setCookie(res, RT, rt, Duration.ofMillis(refreshExpMs), secureCookie);
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

    /** 로그아웃*/
    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res, boolean secureCookie) {
        cleanupTokensAndCookies(req, res, secureCookie);
    }
}
