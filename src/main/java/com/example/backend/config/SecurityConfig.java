package com.example.backend.config;

import com.example.backend.config.token.TokenFilter;
import com.example.backend.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC ={
            "/api/register",
            "/api/login",
            "/actuator/**",
            "/socket"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenFilter tokenFilter(TokenService tokenService) {
        return new TokenFilter(tokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // ปิด CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll() // อนุญาตให้เข้า endpoint นี้ โดยไม่ต้อง login
                        .anyRequest().authenticated() // ทุก endpoint อื่น ๆ ต้อง login ก่อน
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class); //ตรวจสอบ tokenFilter UsernamePasswordAuthenticationFilter
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 