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
            @Value("${toss.api-host:https://api.tosspayments.com}") String apiHost
    ) {
        return WebClient.builder()
                .baseUrl(apiHost)
                .build();
    }
}



