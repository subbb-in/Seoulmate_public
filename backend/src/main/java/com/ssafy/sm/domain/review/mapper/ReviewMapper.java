package com.ssafy.sm.domain.review.mapper;

import com.ssafy.sm.domain.review.dto.ReviewImage;
import com.ssafy.sm.domain.review.dto.ReviewRequest;
import com.ssafy.sm.domain.review.dto.ReviewResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    // 리뷰 작성
    void saveReview(ReviewRequest.Create createDto);

    // 특정 장소의 리뷰 목록 조회
    List<ReviewResponse> findReviewsByPlaceNo(Long placeNo);

    // 리뷰 상세 조회 (권한 확인용)
    ReviewResponse findReviewByNo(Long reviewNo);

    // 리뷰 수정
    int updateReview(ReviewRequest.Update updateDto);

    // 리뷰 삭제 (Soft Delete)
    int deleteReview(Long reviewNo);

    // 장소 평점 재계산 및 업데이트 (트리거 역할)
    void updatePlaceRating(Long placeNo);

    // 삭제된 리뷰인지 확인하기 위해 placeNo 조회
    Long findPlaceNoByReviewNo(Long reviewNo);

    // 이미지 정보 저장
    void saveReviewImage(ReviewImage reviewImage);
    // 리뷰별 이미지 목록 조회
    List<ReviewImage> findImagesByReviewNo(Long reviewNo);
    // 파일 삭제를 위한 파일명 조회
    List<String> findStoredNamesByReviewNo(Long reviewNo);

    // 내가 쓴 리뷰 목록 조회(Mypage용)
    List<ReviewResponse> findReviewsByUserNo(Long userNo);
}