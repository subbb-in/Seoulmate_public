package com.ssafy.sm.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class ReviewRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Create {
        private Long reviewNo;
        private Long placeNo;
        private Long userNo; // 세션에서 주입
        private Integer rating;
        private String content;
        private boolean isHidden; // Soft Delete 구현을 위한 숨김 변수
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Update {
        private Long reviewNo;
        private Integer rating;
        private String content;
        private boolean isHidden;
    }
}
