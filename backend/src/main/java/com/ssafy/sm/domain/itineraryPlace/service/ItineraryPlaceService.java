package com.ssafy.sm.domain.itineraryPlace.service;

import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itineraryPlace.dto.ItineraryPlaceRequest;

import java.util.List;

public interface ItineraryPlaceService {

    // 일정에 장소 추가
    List<ItineraryResponse.ItinPlaceDetail> addItineraryPlace(Long userNo, ItineraryPlaceRequest.Create createDto);

    // 일정 속 장소 삭제
    List<ItineraryResponse.ItinPlaceDetail> removeItineraryPlace(Long userNo, Long itinNo, Long itinPlaceNo);

    List<ItineraryResponse.ItinPlaceDetail> updateItineraryPlaceOrder(Long userNo, Long itinNo, List<ItineraryPlaceRequest.UpdateOrder> orderList);
}
