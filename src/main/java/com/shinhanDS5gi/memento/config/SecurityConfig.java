package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (API 서버에서는 보통 비활성화)
                .csrf(csrf -> csrf.disable())

                // HTTP 기본 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())

                // 폼 로그인 비활성화
                .formLogin(formLogin -> formLogin.disable())

                // 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 문지기가 사용할 루트 경로와, 표준 Actuator 경로만 허용
                        .requestMatchers("/", "/actuator/health").permitAll()
                        .requestMatchers("/api/**").permitAll() // 모든 /api/ 시작 요청 허용

                        // 여기에 기존에 사용하시던 다른 경로 규칙들을 추가하세요
                        // 예: .requestMatchers("/api/auth/**").permitAll()
                        // 예: .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}