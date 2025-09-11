package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /* 전역으로 API 경로에 '/api' 접두사 추가. 단, @NoApiPrefix 어노테이션이 붙은 컨트롤러는 제외 */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api",
                HandlerTypePredicate.forBasePackage("com.shinhanDS5gi.memento.controller")
                        .and(HandlerTypePredicate.forAnnotation(NoApiPrefix.class).negate())
        );
    }
}