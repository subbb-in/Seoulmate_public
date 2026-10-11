package com.ssafy.sm.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class CacheConfig {
    // 매일 오전 11시 10분에 환율 캐시 비움
    @Scheduled(cron = "0 10 11 * * *")
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void evictExchangeCache() {
        log.info("Evicting Exchange Rates Cache");
    }
}
