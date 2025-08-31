package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class WebConfig {
    @Bean
    // 외부 API 호출용 RestTemplate 생성
    public RestTemplate restTemplate() { return new RestTemplate(); }
}



