package com.ssafy.sm.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReviewResponse {
    private Long reviewNo;
    private Long placeNo;
    private Long userNo;
    private String userNickname; // 작성자 닉네임 (JOIN으로 조회)
    private String placeTitle; // 마이페이지에서 보여줄 장소 명
    private Integer rating;
    private String content;
    private boolean isHidden;
    private LocalDateTime createdAt;
    private List<ReviewImage> images;
}