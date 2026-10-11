package com.ssafy.sm.domain.ai.dto;

import com.ssafy.sm.domain.place.dto.PlaceDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class AiMappedDto {
    private String title;
    private List<MappedDayPlan> itinerary;

    @Getter @Setter @ToString
    public static class MappedDayPlan {
        private int day;
        private List<MappedPlace> places; // 텍스트가 아닌 실제 장소 객체 리스트
    }

    @Getter @Setter @ToString
    public static class MappedPlace {
        private PlaceDto.Detail placeDetail; // DB 장소 정보
        private String moveType;
        private int moveMinutes;
    }
}