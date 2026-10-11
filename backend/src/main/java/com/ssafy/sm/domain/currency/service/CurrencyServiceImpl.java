package com.ssafy.sm.domain.currency.service;

import com.ssafy.sm.domain.currency.dto.CurrencyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService{
    @Value("${exim.api.key:}")
    private String authKey;

    private final String BASE_URL = "https://oapi.koreaexim.go.kr/site/program/financial/exchangeJSON";
    private final RestClient restClient = RestClient.builder().baseUrl(BASE_URL).build();

    @Override
    @Cacheable(value = "exchangeRates", unless = "#result.isEmpty()")
    public List<CurrencyDto> getExchangeRates() {
        log.info("환율 API 호출 시작 (Cache miss)");
        LocalDate targetDate = LocalDate.now();

        // 최대 7일까지 역추적
        for (int i = 0; i < 7; i++) {
            String searchDate = targetDate.minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            List<CurrencyDto> rates = fetchFromEximApi(searchDate);

            if (rates != null && !rates.isEmpty()) {
                log.info("{}일자 환율 정보를 성공적으로 가져왔습니다.", searchDate);
                return rates;
            }
            log.warn("{}일자 데이터 없음, 이전 날짜로 재시도합니다.", searchDate);
        }
        return Collections.emptyList();
    }

    private List<CurrencyDto> fetchFromEximApi(String searchDate) {
            try {
                CurrencyDto[] response = restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("authkey", authKey)
                                .queryParam("searchdate", searchDate)
                                .queryParam("data", "AP01")
                                .build())
                        .retrieve()
                        .body(CurrencyDto[].class);

                if (response == null || response.length == 0 || response[0].getResult() == null) {
                    return null;
                }

                List<String> targetCurrencies = Arrays.asList("USD", "EUR", "CNH", "JPY(100)", "IDR(100)");

                return Arrays.stream(response)
                        .filter(dto -> targetCurrencies.contains(dto.getCurUnit()))
                        .collect(Collectors.toList());

            } catch (Exception e) {
                log.error("API 호출 중 오류 발생 (날짜: {}): {}", searchDate, e.getMessage());
                return null;
            }
    }
}
