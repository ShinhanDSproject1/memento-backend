package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* CORS (Cross-Origin Resource Sharing) 정책 위반 문제 해결을 위한 Config */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로(/map/**, /mento/** 등)에 대해
                .allowedOrigins("*")  // 모든 주소(Origin)에서의 요청을 허용   --> 나중에는 실제 서비스 할 https://memento.com 만 허용하도록 변경 필요!
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메서드를 지정
                .allowedHeaders("*"); // 모든 헤더를 허용
    }
}