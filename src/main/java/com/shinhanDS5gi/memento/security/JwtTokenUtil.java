package com.shinhanDS5gi.memento.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
//JWT 생성/검증에 필요한 모든 유틸
public class JwtTokenUtil {

    private static final String C_VER   = "ver";

    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.accessTokenExpiration}") private long accessExpMs;
    @Value("${jwt.refreshTokenExpiration}") private long refreshExpMs;
    @Value("${jwt.same-site:Lax}") private String sameSite;

    //cookie 만들고 헤더 추가
    public void setCookie(HttpServletResponse res, String name, String val, Duration maxAge, boolean secure) {
        ResponseCookie c = ResponseCookie.from(name, val)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, c.toString());
    }

    //cookie 삭제
    public void clearCookie(HttpServletResponse res, String name, boolean secure) {
        ResponseCookie c = ResponseCookie.from(name, "")
                .httpOnly(true).
                secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, c.toString());
    }

    //쿠키 안에 들어있는 AT/RT 읽어서 검증
    public String readCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        return Arrays.stream(req.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue).findFirst().orElse(null);
    }

    //Redis에서 쓸 키 이름을 규격화
    public String rtKey(String sub)    { return "rt:"    + sub; }
    public String atblkKey(String jti) { return "atblk:" + jti; }

    // ${JWT_SECRET}를 SecretKey 객체로 변환
    public SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //Access Token 생성
    public String createAccessToken(String username, int tokenVersion,  String memberType){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("type", memberType)
                .claim(C_VER, tokenVersion)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessExpMs)))
                .signWith(key())
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String username, int tokenVersion){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(C_VER, tokenVersion)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshExpMs)))
                .signWith(key())
                .compact();
    }

    //토큰에서 Claims(정보) 추출
    public Claims claims(String token){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //유효한 토큰 인지 확인
    public boolean validate(String token) {
        try {
            claims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //토큰이 만료 되었는지 확인
    public boolean isExpired(String token){
        return claims(token).getExpiration().before(new Date());
    }

    // 블랙리스트 => at 토큰을 즉시 제거할수 있게 해놓음
    public String getJti(String token)            { return claims(token).getId(); }
    public String getSubject(String token)        { return claims(token).getSubject(); }
    public Date   getExpiration(String token)     { return claims(token).getExpiration(); }



    //토큰이 만료되기까지 남은 시간 계산
    public long remainingMs(String token) {
        long left = getExpiration(token).getTime() - System.currentTimeMillis();
        return Math.max(left, 0);
    }
}

