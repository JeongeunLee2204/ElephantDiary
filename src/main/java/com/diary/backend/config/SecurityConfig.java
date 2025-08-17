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
                // 0) RequestCache 끄기: 정적 리소스 요청이 세션에 저장되어 state 꼬이는 것 방지
                .requestCache(cache -> cache.disable())

                // 1) 공개 경로
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/css/**", "/js/**",
                                "/favicon.ico",
                                "/manifest.webmanifest",
                                // 누락되어 로그인 루프 유발하던 정적 리소스
                                "/vite.svg",
                                "/default-ui.css",
                                // 로그인/에러/OAuth2 엔드포인트는 반드시 공개
                                "/login", "/login/**",
                                "/oauth2/**",
                                "/error"
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 2) OAuth2 로그인
                .oauth2Login(oauth -> oauth
                        // 원래 페이지 복구 대신 고정 성공 URL (RequestCache disable과 궁합)
                        .defaultSuccessUrl("/home", true)
                )

                // 3) 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )

                // 4) 세션
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 5) 개발 편의
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
