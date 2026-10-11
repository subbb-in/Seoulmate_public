package com.ssafy.sm.domain.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class PlaceDto {

    // API 응답용
    @Getter @Setter @NoArgsConstructor @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TourApiItem {
        @JsonProperty("contentid") private Long contentId;
        @JsonProperty("contenttypeid") private Integer contentTypeId;
        @JsonProperty("title") private String title;
        @JsonProperty("addr1") private String addr1;
        @JsonProperty("addr2") private String addr2;
        @JsonProperty("zipcode") private String zipcode;
        @JsonProperty("mapx") private Double mapx;
        @JsonProperty("mapy") private Double mapy;
        @JsonProperty("firstimage") private String firstImage;
        @JsonProperty("firstimage2") private String firstImage2;
        @JsonProperty("tel") private String tel;
    }

    // DB 저장용
    @Getter @Setter @NoArgsConstructor @ToString
    public static class Detail {
        private Long placeNo;
        private Long contentId;
        private Integer contentTypeId;

        private String title;      // 영문
        private String addr1;      // 영문
        private String addr2;      // 영문

        private String titleKo;    // [추가] 한국어
        private String addr1Ko;    // [추가] 한국어

        private String zipcode;
        private Double mapx;
        private Double mapy;
        private String firstImage;
        private String firstImage2;
        private String tel;
        private String theme;

        // 평점 필드 (Float or Double)
        private Float rating;

        // 생성자
        public Detail(TourApiItem item) {
            this.contentId = item.getContentId();
            this.contentTypeId = item.getContentTypeId();
            this.title = item.getTitle();
            this.addr1 = item.getAddr1();
            this.addr2 = item.getAddr2();
            this.zipcode = item.getZipcode();
            this.mapx = item.getMapx();
            this.mapy = item.getMapy();
            this.firstImage = item.getFirstImage();
            this.firstImage2 = item.getFirstImage2();
            this.tel = item.getTel();
        }
    }
}