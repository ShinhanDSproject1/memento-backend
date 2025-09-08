package com.shinhanDS5gi.memento.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class TossWebClientConfig {
    // 토스 API와 연결을 위한 WebClient 설정
    @Bean
    public WebClient tossWebClient(
            @Value("${toss.api-host:https://api.tosspayments.com}") String apiHost,
            @Value("${toss.secret-key}") String secretKey
    ) {
        //setBasicAuth는 HTTP Basic 인증 헤더를 설정하는 메서드
        return WebClient.builder()
                .baseUrl(apiHost)
                .defaultHeaders(h -> h.setBasicAuth(secretKey, ""))
                .build();
    }
}

