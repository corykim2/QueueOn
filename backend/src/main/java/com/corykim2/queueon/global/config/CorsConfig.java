package com.corykim2.queueon.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                 // /api로 시작하는 모든 경로에
                .allowedOrigins("http://localhost:5173") // 이 출처는 허용 (프론트)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메서드
                .allowedHeaders("*")                    // 모든 요청 헤더 허용
                .allowCredentials(true);                // 쿠키/인증정보 주고받기 허용
    }
}