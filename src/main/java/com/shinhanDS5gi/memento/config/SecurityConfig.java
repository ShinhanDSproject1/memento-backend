package com.shinhanDS5gi.memento.config;

import com.shinhanDS5gi.memento.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login/**", "/api/auth/refresh", "/api/auth/logout").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 인증 실패/인가 실패 응답 정리(401/403 바디)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> { // 401
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write(
                                    "{\"code\":2001,\"status\":401,\"message\":\"인증 필요 또는 토큰 만료\",\"result\":null}"
                            );
                        })
                        .accessDeniedHandler((req, res, e) -> { // 403
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write(
                                    "{\"code\":2003,\"status\":403,\"message\":\"접근 권한이 없습니다\",\"result\":null}"
                            );
                        })
                );
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
