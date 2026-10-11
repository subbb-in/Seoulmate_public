package com.ssafy.sm.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

public class BoardResponse {
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class List {
        private Long boardNo;
        private String boardTitle;
        private String boardWriter;
        private int viewCnt;
        private LocalDateTime createdAt;
        private String thumbnailPath;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Detail {
        private Long boardNo;
        private String boardTitle;
        private String boardWriter;
        private String boardContent;
        private int viewCnt;
        private LocalDateTime createdAt;
        private Long userNo; // 수정/삭제 권한 확인용 조회
        private java.util.List<BoardImageDto> images;
    }
}
