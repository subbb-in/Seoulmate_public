package com.ssafy.sm.domain.currency.service;

import com.ssafy.sm.domain.currency.dto.CurrencyDto;

import java.util.List;

public interface CurrencyService{
    List<CurrencyDto> getExchangeRates();
}
