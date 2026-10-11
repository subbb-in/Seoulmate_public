package com.ssafy.sm.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class BoardRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Create {
    	public Long boardNo;
        public String boardTitle;
        public String boardContent;
        // boardWriter와 userNo는 session에서 꺼냄
        public String boardWriter;
        private Long userNo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Update {
        private Long userNo;
        private Long boardNo;
        public String boardTitle;
        public String boardContent;
    }



}
