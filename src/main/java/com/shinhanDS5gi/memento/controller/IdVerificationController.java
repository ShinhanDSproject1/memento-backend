package com.shinhanDS5gi.memento.controller;

import com.shinhanDS5gi.memento.common.exception.MentosException;
import com.shinhanDS5gi.memento.common.response.status.BaseExceptionResponseStatus;
import com.shinhanDS5gi.memento.service.IdVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/verifications")
public class IdVerificationController {

    private final IdVerificationService idverificationservice;
    private final Map<String, Map<String,String>> store = new ConcurrentHashMap<>(); //요청데이터 임시 저장

    // 1) 금융결제원 api 요청
    @PostMapping("/request")
    public Map<String, String> request(@RequestBody Map<String,String> body) {
        // UUID.randomUUID(): 보안 토큰 생성 (CSRF 방지 & 요청/응답 매칭)
        String state = UUID.randomUUID().toString().replace("-", "");
        store.put(state, body); // 해당 state에 요청 데이터 임시 저장
        return Map.of(
                "state", state,
                "authorizeUrl", idverificationservice.buildAuthorizeUrl(state)
        );
    }

    // 2) 확인(콜백) API → GET /member/verifications?code=...&state=...
    @GetMapping
    public ResponseEntity<Map<String,Object>> confirm(@RequestParam String code, // 인가코드
                                                      @RequestParam String state) { // 요청시 발급했던 state
        Map<String,String> saved = store.remove(state); // 요청데이터 꺼내면서 제거
        if (saved == null) { // 만료/위조 방지
            return ResponseEntity.ok(Map.of("code", 410, "message", "세션만료 또는 잘못된 state")); // 상태 메시지 반환
        }

        String token = idverificationservice.exchangeToken(code); // 인가코드로 액세스 토큰 교환(목 구현)
        String holderName = idverificationservice.realName(token, saved); // 실명조회 호출(목 구현)

        if (holderName == null) { // 테스트 데이터 없거나 실패
            return ResponseEntity.ok(Map.of("code", 202, "message", "실명조회 실패(테스트 데이터 없음)")); // 실패 응답
        }

        String userName = saved.get("name"); // 사용자가 입력한 이름
        Boolean match = (userName == null) ? null : idverificationservice.nameMatches(holderName, userName); // 이름 일치 여부

        if (Boolean.FALSE.equals(match)) {
            // 이름 불일치 → 공통 예외 던지기
            throw new MentosException(BaseExceptionResponseStatus.REALNAME_MISMATCH);
        }

        Map<String,Object> res = new LinkedHashMap<>();
        res.put("code", 200);
        res.put("message", match == null ? "실명조회 성공(비교 이름 없음)" : "본인인증 성공");
        res.put("account_holder_name", holderName);
        res.put("match", match);
        return ResponseEntity.ok(res);
    }
}
