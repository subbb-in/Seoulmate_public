package com.ssafy.sm.domain.itinerary.repository;

import com.ssafy.sm.domain.itinerary.entity.Itinerary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

    // 유저별 일정 페이징 조회 (삭제되지 않은 것만)
    Page<Itinerary> findAllByUser_UserNoAndIsDeletedFalseOrderByCreatedAtDesc(Long userNo, Pageable pageable);

    // 유저별 일정 개수
    int countByUser_UserNoAndIsDeletedFalse(Long userNo);

    // 일정 단건 조회 (유저 + 삭제 여부 확인)
    Optional<Itinerary> findByItinNoAndUser_UserNoAndIsDeletedFalse(Long itinNo, Long userNo);
}
