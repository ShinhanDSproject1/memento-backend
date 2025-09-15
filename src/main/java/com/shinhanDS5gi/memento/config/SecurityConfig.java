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
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 시스템 운영 및 인증/인가 관련 API는 항상 허용
                        .requestMatchers("/", "/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // 로그인, 회원가입, 토큰 재발급
                        .requestMatchers("/api/payments/success", "/api/payments/fail").permitAll() // 결제 Callback

                        // 읽기 전용 공개 API (GET 요청만 허용)
                        .requestMatchers(HttpMethod.GET,
                                "/api/mentos/category/**",
                                "/api/mentos/detail/**",
                                "/api/mento/reviews/**",
                                "/api/map/**",
                                "/api/config/**",
                                "/api/reservation/availability/**"
                        ).permitAll()

                        // 관리자만 접근 가능한 API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 나머지 모든 요청은 반드시 인증 필요
                        .anyRequest().authenticated()
                )

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
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

