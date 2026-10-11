package com.ssafy.sm.domain.review.repository;

import com.ssafy.sm.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    // 리뷰별 이미지 목록 조회
    List<ReviewImage> findAllByReview_ReviewNo(Long reviewNo);
}
