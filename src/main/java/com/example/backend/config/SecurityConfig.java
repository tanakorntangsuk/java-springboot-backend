package com.example.backend.config;

import com.example.backend.config.token.TokenFilter;
import com.example.backend.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC = {
            "/api/register",
            "/api/login",
            "/actuator/**",
            "/socket/**",
            "/api/activate"
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
                .cors(Customizer.withDefaults()) // เปิดให้ระบบ CORS ทำงาน
                .csrf(csrf -> csrf.disable())  // ปิด CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll() // อนุญาตให้เข้า endpoint นี้ โดยไม่ต้อง login
                        .anyRequest().authenticated() // ทุก endpoint อื่น ๆ ต้อง login ก่อน
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class); //ตรวจสอบ tokenFilter UsernamePasswordAuthenticationFilter
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}