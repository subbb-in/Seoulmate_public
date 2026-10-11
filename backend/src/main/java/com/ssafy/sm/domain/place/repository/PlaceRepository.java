package com.ssafy.sm.domain.place.repository;

import com.ssafy.sm.domain.place.entity.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // contentId로 장소 찾기 (Upsert 로직용)
    Optional<Place> findByContentId(Long contentId);

    // 전체 장소 조회 (제목순 정렬) - findAllPlaces 대체
    List<Place> findAllByOrderByTitleAsc();

    // 제목 검색 및 길이순 정렬 - findPlaceByTitle 대체
    // MySQL 등 대부분의 DB에서 LENGTH() 함수 지원
    @Query("SELECT p FROM Place p " +
            "WHERE p.title LIKE CONCAT('%', :keyword, '%') " +
            "OR p.titleKo LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY LENGTH(p.title) ASC")
    List<Place> findByTitleContainingOrderByLength(@Param("keyword") String keyword, Pageable pageable);
}