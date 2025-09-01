package com.diary.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 정적 리소스 요청을 RequestCache에 쌓지 않도록
                .requestCache(cache -> cache.disable())

                .authorizeHttpRequests(auth -> auth
                        // 정적/루트/빌드 산출물은 공개
                        .requestMatchers(
                                "/", "/index.html",
                                "/assets/**", "/static/**",
                                "/css/**", "/js/**",
                                "/favicon.ico",
                                "/manifest.webmanifest",
                                "/vite.svg",
                                "/default-ui.css"
                        ).permitAll()

                        // OAuth2 로그인/콜백/에러 등 공개
                        .requestMatchers(
                                "/login", "/login/**",
                                "/oauth2/**", "/login/oauth2/**",
                                "/error"
                        ).permitAll()

                        // API만 인증 요구
                        .requestMatchers("/api/**").authenticated()

                        // 그 외(프론트 라우트 포함)는 공개 → ReactForwardController가 index.html 로 포워딩
                        .anyRequest().permitAll()
                )

                // OAuth2 로그인: 이전 페이지 복구를 쓰거나, 존재하는 경로로 고정
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/list", true)
                )

                // 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )

                // 세션
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 개발 편의
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
