package com.ssafy.sm.domain.itinerary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class ItineraryRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Create {
        private Long itinNo;
        private String itinTitle; // 여행 이름
        private int dayCnt; // 여행일수
        private String thumbnail; // 썸네일 이미지 URL

        // session에서 받아옴
        private Long userNo;
    }

    // AI 추천 일정 저장용 DTO
    @Getter @Setter @NoArgsConstructor @ToString
    public static class SaveAiItinerary {
        private String title;       // 여행 제목
        private int dayCnt;         // 총 일수
        private List<DayPlan> itinerary; // 일자별 리스트
        private Long userNo;        // 세션 주입
        private String thumbnail;   // AI 일정도 썸네일 저장 가능하도록 추가

        @Getter @Setter @NoArgsConstructor @ToString
        public static class DayPlan {
            private int day;
            private List<PlaceInfo> places;
        }

        @Getter @Setter @NoArgsConstructor @ToString
        public static class PlaceInfo {
            private Long placeNo;
            private String moveType;
            private int moveMinutes;
        }
    }

    public static class Update {

    }
}