package com.ssafy.sm.domain.itinerary.service;

import com.ssafy.sm.domain.itinerary.dto.ItineraryRequest;
import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;

public interface ItineraryService {
    // 일정 생성
    ItineraryResponse.Page createItinerary(ItineraryRequest.Create createDto, int page, int size);

    // 페이지네이션
    ItineraryResponse.Page getPagedItineraries(Long userNo, int page, int size);

    // 일정 상세 조회
    //List<ItineraryResponse.ItinPlaceDetail> getItinerary(Long userNo, Long itinNo);
    ItineraryResponse.DetailWithPlaces getItinerary(Long userNo, Long itinNo);

    // 일정 삭제
    void deleteItinerary(Long itinNo, Long userNo);

    // AI 일정 저장
    Long saveAiItinerary(ItineraryRequest.SaveAiItinerary saveDto);
}
