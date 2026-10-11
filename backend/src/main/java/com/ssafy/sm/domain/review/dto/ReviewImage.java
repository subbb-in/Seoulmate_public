package com.ssafy.sm.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReviewImage {
    private Long reviewImgNo;
    private Long reviewNo;
    private String reviewImgName;
    private String originName;
    private LocalDateTime createAt;

    public ReviewImage(String originName, String reviewImgName) {
        this.originName = originName;
        this.reviewImgName = reviewImgName;
    }
}