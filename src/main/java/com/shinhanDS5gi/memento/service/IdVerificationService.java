package com.shinhanDS5gi.memento.service;

import java.util.Map;

public interface IdVerificationService {
    String buildAuthorizeUrl(String state); // 실명인증 동의 화면 uri 생성
    String exchangeToken(String code);      // (지금은 Mock) 인가코드→액세스 토큰 교환
    String realName(String accessToken, Map<String,String> in); // (지금은 Mock) 실명조회 호출(더미)

    default boolean nameMatches(String apiName, String userName) { // 응답 이름과 사용자가 입력한 이름 비교
        if (apiName == null || userName == null) return false;
        return apiName.replaceAll("\\s+","").equalsIgnoreCase(userName.replaceAll("\\s+","")); // 공백 제거·대소문자 무시 비교
    }
}
