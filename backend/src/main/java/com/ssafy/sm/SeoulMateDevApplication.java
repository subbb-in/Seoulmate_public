package com.ssafy.sm;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
public class SeoulMateDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeoulMateDevApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        // 1. Apache HttpClient 생성
        HttpClient httpClient = HttpClients.custom()
                .disableCookieManagement() // 쿠키 관리 끔 (API 호출엔 불필요)
                .build();

        // 2. Factory에 주입
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // 3. 타임아웃 설정 (AI 응답이 느릴 수 있으므로 넉넉하게)
        factory.setConnectTimeout(5000); // 연결 5초
        // [중요] Factory 설정이 버전마다 메소드명이 다를 수 있음.
        // 에러가 난다면 setConnectionRequestTimeout 등을 확인해야 함.

        return new RestTemplate(factory);
    }

}
