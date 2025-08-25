package com.shinhanDS5gi.memento.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    // 비밀번호 해시 passwordEncoder 빈 생성
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Postman 확인용 임시 비밀번호 해시 출력용 (확인 후 삭제해야함!)
    @Bean
    CommandLineRunner printHashes(PasswordEncoder pe) {
        return args -> {
            System.out.println("ENC_1234 = " + pe.encode("1234"));
            System.out.println("ENC_admin = " + pe.encode("admin"));
        };
    }
}

