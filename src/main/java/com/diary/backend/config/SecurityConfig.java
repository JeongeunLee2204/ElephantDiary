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
                // 1) 공개 리소스와 루트는 반드시 permitAll
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                 // 로그아웃 성공 후 도착지
                                "/index.html",
                                "/assets/**",        // Vite 빌드 산출물
                                "/css/**", "/js/**",
                                "/favicon.ico",
                                "/manifest.webmanifest"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 2) OAuth2 로그인 성공 시 인증이 필요한 화면으로 이동
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/home", true)
                )

                // 3) 전체 페이지 이동 로그아웃: 세션/쿠키 정리 후 공개 경로("/")로 리다이렉트
                .logout(logout -> logout
                        .logoutUrl("/logout")                  // window.location.assign("/logout")로 호출
                        .logoutSuccessUrl("/")                 // 공개 경로여야 함
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )

                // 4) 필요 시만 세션 생성
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 데브 편의: CSRF 비활성화(POST 없이 GET /logout도 동작). 운영에선 재검토 권장
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
