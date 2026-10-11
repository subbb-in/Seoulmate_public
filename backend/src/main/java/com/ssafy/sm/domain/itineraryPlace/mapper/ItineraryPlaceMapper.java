package com.ssafy.sm.domain.itineraryPlace.mapper;

import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itineraryPlace.dto.ItineraryPlaceRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItineraryPlaceMapper {
    // 장소 추가
    void saveItineraryPlace(ItineraryPlaceRequest.Create createDto);

    // 장소 삭제
    int deleteByItineraryAndPlace(@Param("itinNo") Long itinNo, @Param("itinPlaceNo") Long itinPlaceNo);

    // [추가됨] 순서 변경 업데이트
    void updateDetailOrder(@Param("itinPlaceNo") Long itinPlaceNo,
                           @Param("itinNo") Long itinNo,
                           @Param("dayOrder") int dayOrder,
                           @Param("orderInDay") int orderInDay);

    // 전체 조회 (삭제나 업데이트 후 최신 리스트 반환용)
    List<ItineraryResponse.ItinPlaceDetail> findAllByItineraryNo(Long itinNo);
}
