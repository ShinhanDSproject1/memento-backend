package com.shinhanDS5gi.memento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordConfig {
    //비밀번호 해시(보완을 위해) ServiceImpl에서 @RequiredArgsConstructor로 PasswordEncoder를 주입받아 사용
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
