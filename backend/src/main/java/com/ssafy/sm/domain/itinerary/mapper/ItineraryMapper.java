package com.ssafy.sm.domain.itinerary.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.sm.domain.itinerary.dto.ItineraryRequest;
import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItineraryMapper {
	// 여행 일정 작성
	void saveItinerary(ItineraryRequest.Create createDto);

	// 페이징
	List<ItineraryResponse.GetAll> findAllItineraryPaged(Map<String, Object> params);

	// user에 따른 일정 개수 반환
	int countItinerary(Long userNo);

	// 일정 기본 정보(제목, 날짜, 인원, 테마 등) 조회
	ItineraryResponse.Basic findItineraryBasic(@Param("itinNo") Long itinNo, @Param("userNo") Long userNo);

	// 일정에 포함된 장소 리스트 조회
	List<ItineraryResponse.ItinPlaceDetail> findPlacesByItinerary(@Param("itinNo") Long itinNo);

	// 여행 일정 삭제
	int deleteByUserNoAndItinNo(@Param("itinNo") Long itinNo, @Param("userNo") Long userNo);
}
