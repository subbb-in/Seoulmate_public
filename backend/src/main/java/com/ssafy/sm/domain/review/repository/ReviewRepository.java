package com.ssafy.sm.domain.review.repository;

import com.ssafy.sm.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 장소의 리뷰 목록 조회 (삭제되지 않은 것만)
    List<Review> findAllByPlace_PlaceNoAndIsDeletedFalseOrderByCreatedAtDesc(Long placeNo);

    // 리뷰 단건 조회 (삭제되지 않은 것만)
    Optional<Review> findByReviewNoAndIsDeletedFalse(Long reviewNo);

    // 내가 쓴 리뷰 목록 조회 (Mypage용)
    List<Review> findAllByUser_UserNoAndIsDeletedFalseOrderByCreatedAtDesc(Long userNo);

    // 특정 장소의 평균 평점 계산
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.place.placeNo = :placeNo AND r.isDeleted = false")
    Float calculateAverageRatingByPlaceNo(@Param("placeNo") Long placeNo);
}
