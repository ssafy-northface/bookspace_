package com.bookspace.global.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 * - CORS, Interceptors, Message Converter 설정
 * - 정적 리소스 매핑, Multipart 설정 (파일 업로드 등)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns(
                        // ngrok 무료 도메인
                        "http://localhost:5173",
                        "https://*.ngrok-free.app",
                        "http://localhost:4173",
                        "https://*.ngrok-free.dev"

                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Refresh-Token")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
