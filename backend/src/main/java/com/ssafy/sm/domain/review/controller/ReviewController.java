package com.ssafy.sm.domain.review.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.review.dto.ReviewRequest;
import com.ssafy.sm.domain.review.dto.ReviewResponse;
import com.ssafy.sm.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 작성 (POST /api/reviews)
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Void>> createReview(
            @RequestPart(value = "review") ReviewRequest.Create createDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long userNo = SecurityUtil.getCurrentUserNo();
        createDto.setUserNo(userNo);

        try {
            reviewService.createReview(createDto, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("리뷰 작성 성공.", null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        }
    }

    // 특정 장소의 리뷰 목록 조회 (GET /api/reviews/place/{placeNo})
    @GetMapping("/place/{placeNo}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByPlace(@PathVariable("placeNo") Long placeNo) {
        List<ReviewResponse> list = reviewService.getReviewsByPlace(placeNo);
        return ResponseEntity.ok(ApiResponse.success("리뷰 목록 조회 성공", list));
    }

    // 리뷰 수정 (PATCH /api/reviews/{reviewNo})
    @PatchMapping("/{reviewNo}")
    public ResponseEntity<ApiResponse<Void>> updateReview(@PathVariable Long reviewNo,
                                                          @RequestBody ReviewRequest.Update updateDto) {
        Long userNo = SecurityUtil.getCurrentUserNo();
        ReviewResponse existingReview = reviewService.getReview(reviewNo);

        if (existingReview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("해당 리뷰를 찾을 수 없습니다"));
        }

        if (!userNo.equals(existingReview.getUserNo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("수정 권한이 없습니다."));
        }

        updateDto.setReviewNo(reviewNo);
        reviewService.updateReview(updateDto);
        return ResponseEntity.ok(ApiResponse.success("리뷰 수정 완료", null));
    }

    // 리뷰 삭제 (DELETE /api/reviews/{reviewNo})
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewNo) {
        Long userNo = SecurityUtil.getCurrentUserNo();
        ReviewResponse existingReview = reviewService.getReview(reviewNo);

        if (existingReview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("리뷰를 찾을 수 없습니다."));
        }

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!userNo.equals(existingReview.getUserNo()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("삭제 권한이 없습니다."));
        }

        reviewService.deleteReview(reviewNo);
        return ResponseEntity.ok(ApiResponse.success("리뷰 삭제 성공", null));
    }
}
