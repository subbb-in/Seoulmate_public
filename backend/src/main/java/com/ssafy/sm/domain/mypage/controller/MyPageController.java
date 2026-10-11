package com.ssafy.sm.domain.mypage.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.bookmark.dto.BookmarkResponse;
import com.ssafy.sm.domain.bookmark.service.BookmarkService;
import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itinerary.service.ItineraryService;
import com.ssafy.sm.domain.review.dto.ReviewResponse;
import com.ssafy.sm.domain.review.service.ReviewService;
import com.ssafy.sm.domain.user.dto.UserResponse;
import com.ssafy.sm.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;
    private final ItineraryService itineraryService;

    // 1. 내 프로필 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        Long userNo = SecurityUtil.getCurrentUserNo();
        UserResponse user = userService.getUser(userNo);
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", user));
    }

    // 2. 내가 쓴 리뷰 목록 조회
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews() {
        Long userNo = SecurityUtil.getCurrentUserNo();
        List<ReviewResponse> reviews = reviewService.getMyReviews(userNo);
        return ResponseEntity.ok(ApiResponse.success("내 리뷰 목록 조회 성공", reviews));
    }

    // 3. 내가 찜한 장소 목록 조회
    @GetMapping("/places")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getMyBookmarks() {
        Long userNo = SecurityUtil.getCurrentUserNo();
        List<BookmarkResponse> bookmarks = bookmarkService.getMyBookmarks(userNo);
        return ResponseEntity.ok(ApiResponse.success("찜 목록 조회 성공", bookmarks));
    }

    // 4. 내 일정 목록 조회
    @GetMapping("/itineraries")
    public ResponseEntity<ApiResponse<ItineraryResponse.Page>> getMyItineraries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userNo = SecurityUtil.getCurrentUserNo();
        ItineraryResponse.Page result = itineraryService.getPagedItineraries(userNo, page, size);
        return ResponseEntity.ok(ApiResponse.success("내 일정 목록 조회 성공", result));
    }
}
