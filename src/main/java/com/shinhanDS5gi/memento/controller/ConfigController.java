package com.shinhanDS5gi.memento.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Value("${kakao.js.api.key}")
    private String kakaoJsApiKey;

    /* 프론트엔드에 카카오맵 JavaScript API 키를 전달하는 API */
    @GetMapping("/maps-key")
    public ResponseEntity<Map<String, String>> getMapsApiKey() {
        return ResponseEntity.ok(Map.of("apiKey", kakaoJsApiKey));
    }
}