package com.ssafy.sm.domain.currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrencyDto {
    @JsonProperty("result")
    private Integer result; // 결과(1: 성공)

    @JsonProperty("cur_unit")
    private String curUnit; // 통화 코드(USD, EUR 등)

    @JsonProperty("cur_nm")
    private String curNm; // 국가, 통화 명

    @JsonProperty("ttb")
    private String ttb; // 전신환(송금) 받을 때

    @JsonProperty("tts")
    private String tts; // 전신환(송금) 보낼 때

    @JsonProperty("deal_bas_r")
    private String dealBasR; // 매매 기준율

    @JsonProperty("bkpr")
    private String bkpr; // 장부가격
}
