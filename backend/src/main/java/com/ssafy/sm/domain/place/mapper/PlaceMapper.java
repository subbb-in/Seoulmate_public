package com.ssafy.sm.domain.place.mapper;

import com.ssafy.sm.domain.place.dto.PlaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaceMapper {
    // 1. 영문 장소 저장
    void saveEnglishPlace(PlaceDto.Detail place);

    // 2. 한국어 데이터 업데이트
//    void updateKoreanInfo(@Param("contentId") Long contentId,
//                          @Param("titleKo") String titleKo,
//                          @Param("addr1Ko") String addr1Ko);

    // 장소 상세 조회
    PlaceDto.Detail findPlaceByNo(Long placeNo);

    // 장소 전체 조회
    List<PlaceDto.Detail> findAllPlaces();

    // AI 매핑용: 이름으로 장소 검색
    List<PlaceDto.Detail> findPlaceByTitle(@Param("keyword") String keyword);
}