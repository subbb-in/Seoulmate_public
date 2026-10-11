package com.ssafy.sm.domain.currency.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.currency.dto.CurrencyDto;
import com.ssafy.sm.domain.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ApiResponse<List<CurrencyDto>> getExchangeRates() {
        List<CurrencyDto> rates = currencyService.getExchangeRates();
        return ApiResponse.success("success", rates);
    }
}
