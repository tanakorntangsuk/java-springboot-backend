package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // ปิด CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/register", "api/login").permitAll() // อนุญาตให้เข้า endpoint นี้ โดยไม่ต้อง login
                        .anyRequest().authenticated() // ทุก endpoint อื่น ๆ ต้อง login ก่อน
                );
        return http.build();
    }
} 