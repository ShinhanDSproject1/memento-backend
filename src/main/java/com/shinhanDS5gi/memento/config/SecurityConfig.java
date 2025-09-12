package com.shinhanDS5gi.memento.config;

import com.shinhanDS5gi.memento.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (API 서버라면 비활성화 권장)
                .csrf(csrf -> csrf.disable())

                // CORS 설정 (기본값)
                .cors(Customizer.withDefaults())

                // HTTP 기본 인증/폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                // 세션을 STATELESS로 설정 (JWT 사용 시 필수)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 토큰 갱신, 로그아웃 허용
                        .requestMatchers("/auth/login/**", "/auth/refresh", "/auth/logout").permitAll()

                        // 기존 develop 쪽 설정(헬스 체크 등)
                        .requestMatchers("/", "/actuator/health").permitAll()
                        .requestMatchers("/api/**").permitAll() // 필요 시 수정 가능

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
