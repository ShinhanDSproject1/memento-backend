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
                .authorizeHttpRequests(auth -> auth
                        // CORS Preflight 요청은 언제나 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Docker Health Check를 위한 경로들을 최우선으로 허용
                        .requestMatchers("/", "/actuator/health").permitAll()

                        // 로그인, 회원가입, 토큰 갱신 등 인증 API 허용
                        .requestMatchers("/api/auth/**").permitAll()

                        // 누구나 볼 수 있는 공개 API들 허용
                        .requestMatchers(HttpMethod.GET,
                                "/api/mentos/category/**",   // 카테고리별 멘토스 목록 조회
                                "/api/mentos/detail/**",     // 멘토스 상세 조회
                                "/api/mento/reviews/**",     // 특정 멘토의 리뷰 목록 조회
                                "/api/map/mentos",           // 주변 멘토 조회
                                "/api/config/maps-key",      // 카카오맵 API 키 조회
                                "/api/reservation/availability/**" // 멘토의 예약 가능 시간 조회
                        ).permitAll()

                        // 5. 위에서 허용한 경로 외 나머지 모든 요청은 반드시 인증 필요
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

