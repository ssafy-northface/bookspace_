package com.bookspace.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Spring 설정 클래스
@EnableWebSecurity // Spring Security 활성화
public class SecurityConfig {

    // 비밀번호 해싱용 (암호화)
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // security filter chain 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // 브라우저 기본 폼 요청 공격 방어 (주로 세션에서 사용됨)
                .formLogin(form -> form.disable()) // 기본 폼 (/login) 비활성화 -> (/auth/login)
                .httpBasic(basic -> basic.disable()) // 헤더에 id/pw 실어 보내는 basic 방식 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 X
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/join",
                                "/users/**",
                                "/auth/**"
                        ).permitAll() // 인증 없이 가능
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated() // 나머지 요청은 jwt가 필요함
                );

        return http.build();
    }
}

