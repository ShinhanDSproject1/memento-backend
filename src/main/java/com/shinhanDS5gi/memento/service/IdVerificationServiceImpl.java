package com.shinhanDS5gi.memento.service;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class IdVerificationServiceImpl implements IdVerificationService {

    // 데모용 더미(계좌-이름)
    private static final Map<String, String> MOCK_ACCOUNTS = Map.of(
            "12345678901234", "홍길동",
            "22223333444455", "김철수",
            "99998888777766", "이영희"
    );

    // Client ID
    private static final String CLIENT_ID    = "bded8696-709a-442c-822c-1c109faa6514";
    // 등록된 콜백 URL
    private static final String REDIRECT_URI = "http://localhost:9999/member/verifications";

    @Override
    public String buildAuthorizeUrl(String state) { // 동의 화면 URL 조립
        // scope는 공백 인코딩, redirect_uri도 인코딩
        String scope = URLEncoder.encode("login inquiry transfer oob", StandardCharsets.UTF_8);
        return "https://testapi.openbanking.or.kr/oauth/2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                + "&scope=" + scope
                + "&client_info=" + "test"
                + "&state=" + state // CSRF 방지/세션 식별자
                + "&auth_type=0";
    }

    @Override
    public String exchangeToken(String code) { // 인가코드→토큰 교환 (목)
        return "dummy_access_token_" + code; // 테스트용 토큰 문자열
    }

    @Override
    public String realName(String accessToken, Map<String, String> in) { // 실명조회 (목)
        String acc = in.get("account_num"); // body 키 이름과 일치해야 함
        return MOCK_ACCOUNTS.getOrDefault(acc, null); // 테스트 계좌면 이름 반환, 아니면 null
    }
}
