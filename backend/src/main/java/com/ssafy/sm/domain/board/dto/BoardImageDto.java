package com.ssafy.sm.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BoardImageDto {
    private Long boardImgNo;    // PK
    private Long boardNo;       // FK

    private String originName;   // 원본명
    private String boardImgName; // 저장명 (UUID)
    private String imgPath;      // 경로

    private int sortOrder;       // 순서
    private boolean isThumbnail; // 썸네일 여부

    // 생성자
    public BoardImageDto(String originName, String boardImgName, String imgPath, int sortOrder, boolean isThumbnail) {
        this.originName = originName;
        this.boardImgName = boardImgName;
        this.imgPath = imgPath;
        this.sortOrder = sortOrder;
        this.isThumbnail = isThumbnail;
    }
}