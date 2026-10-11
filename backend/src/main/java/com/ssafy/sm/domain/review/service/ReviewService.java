package com.ssafy.sm.domain.review.service;

import com.ssafy.sm.domain.review.dto.ReviewRequest;
import com.ssafy.sm.domain.review.dto.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    // 리뷰 작성 (파일 첨부 추가)
    void createReview(ReviewRequest.Create createDto, List<MultipartFile> files) throws IOException;

    // 리뷰 목록 조회
    List<ReviewResponse> getReviewsByPlace(Long placeNo);

    // 리뷰 상세
    ReviewResponse getReview(Long reviewNo);

    // 내 리뷰 조회(Mypage용)
    List<ReviewResponse> getMyReviews(Long userNo);

    // 리뷰 수정
    void updateReview(ReviewRequest.Update updateDto);

    // 리뷰 삭제
    void deleteReview(Long reviewNo);
}
