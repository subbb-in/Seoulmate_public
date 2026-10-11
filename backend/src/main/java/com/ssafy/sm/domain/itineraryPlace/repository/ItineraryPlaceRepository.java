package com.ssafy.sm.domain.itineraryPlace.repository;

import com.ssafy.sm.domain.itineraryPlace.entity.ItineraryPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItineraryPlaceRepository extends JpaRepository<ItineraryPlace, Long> {

    // 특정 일정의 모든 장소 조회 (일차순, 방문순)
    List<ItineraryPlace> findAllByItinerary_ItinNoOrderByDayOrderAscOrderInDayAsc(Long itinNo);

    // 특정 일정의 특정 장소 조회 (삭제용)
    Optional<ItineraryPlace> findByItinPlaceNoAndItinerary_ItinNo(Long itinPlaceNo, Long itinNo);
}
