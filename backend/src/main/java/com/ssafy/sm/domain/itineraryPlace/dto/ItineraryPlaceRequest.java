package com.ssafy.sm.domain.itineraryPlace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class ItineraryPlaceRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Create {
        private Long itinPlaceNo;
        private Long itinNo;
        private Long placeNo;
        private int dayOrder;   // 여행 N 일차
        private int orderInDay; // 방문순서
        private String moveType; // 이동수단
        private int moveMinutes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class UpdateOrder {
        private Long itinPlaceNo;
        private int dayOrder;
        private int orderInDay;
    }
}
