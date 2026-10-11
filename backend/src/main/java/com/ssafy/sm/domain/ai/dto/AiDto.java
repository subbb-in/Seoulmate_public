package com.ssafy.sm.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

public class AiDto {

    // [프론트엔드 -> 서버] 요청 DTO
    @Getter
    @Setter
    @ToString
    public static class Request {
        private String destination; // 여행지 (예: 서울)
        private String days;        // 여행 기간 (예: 1박 2일)
        private String theme;       // 여행 테마 (예: 힐링, 맛집, 역사)
        private String companions;  // 동행인 (예: 친구, 연인, 가족)
    }

    // [서버 -> 프론트엔드] 응답 DTO (AI가 짜준 일정)
    @Getter @Setter @ToString @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private String title;       // 추천 코스 제목
        private List<DayPlan> itinerary; // 일자별 계획

        @Getter @Setter @ToString @NoArgsConstructor
        public static class DayPlan {
            private int day;            // 1일차, 2일차...
            private List<PlaceInfo> places; // 방문 장소 리스트
        }

        @Getter @Setter @ToString @NoArgsConstructor
        public static class PlaceInfo {
            private String placeName; //장소 이름
            private String moveType; //이동 수단(BUS, SUBWAY, etc.)
            private int moveMinutes; //이동 시간
        }
    }
}
