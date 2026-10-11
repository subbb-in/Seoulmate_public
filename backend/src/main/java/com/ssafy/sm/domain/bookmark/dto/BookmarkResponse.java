package com.ssafy.sm.domain.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookmarkResponse {
    //찜 정보
    private Long bookmarkNo;
    private LocalDateTime createdAt;
    //장소 정보(리스트에 뿌려줄 정보)
    private Long placeNo;
    private String title;       // 장소명
    private String addr1;       // 주소
    private String firstImage;  // 썸네일 이미지
    private Float rating;      // 평점
}
