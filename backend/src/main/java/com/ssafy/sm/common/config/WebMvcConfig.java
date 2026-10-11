package com.ssafy.sm.common.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir}")
    private String uploadDir;

    /*
        파일 업로드 경로를 웹 URL과 매핑
     */
    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        // 운영 체제에 따른 경로 패턴 정리 : 컴퓨터가 이해하는 실제 파일 경로
        // 1. 운영 체제에 따른 경로 패턴 정리
        // Windows : file:///C://uploads/     MAC : file:/home/uploads
        String resourceLocation = "file:///"+uploadDir;

        // 경로 끝에 슬래시(/)가 없다면 붙여줌(오류 방지)
        if (!uploadDir.endsWith("/")) {
            resourceLocation += "/";
        }

        // 2. URL 매핑
        // 웹 브라우저 요청: http://localhost:8080/uploads/IMAGE_TITLE_HERE.jpg
        // 실제 파일 경로: C:/uploads/IMAGE_TITLE_HERE.jpg
        registry.addResourceHandler("/uploads/**").addResourceLocations(resourceLocation);
    }

    /*
        프론트엔드와의 통신 허용(CORS) 담당
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // .allowedOrigins("http://localhost:3000") // 특정 주소만 허용(보안상 권장)
                .allowedOriginPatterns("*") // 모든 주소에서의 요청 허용 (추후 실제 서비스 시에는 보안 처리 필요)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowCredentials(true); // 쿠키/세션 인증을 위해 필요할 수 있음(JWT 적용 시 확인 필요)
    }
}