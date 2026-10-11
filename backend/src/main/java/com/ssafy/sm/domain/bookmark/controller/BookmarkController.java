package com.ssafy.sm.domain.bookmark.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.bookmark.dto.BookmarkResponse;
import com.ssafy.sm.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    // 찜 & 찜 취소(Toggle)
    // POST /api/bookmarks/{placeNo}
    @PostMapping("/{placeNo}")
    public ResponseEntity<ApiResponse<Boolean>> toggleBookmark(@PathVariable Long placeNo) {
        Long userNo = SecurityUtil.getCurrentUserNo();
        boolean isBookmarked = bookmarkService.toggleBookmark(userNo, placeNo);

        String message = isBookmarked ? "찜 목록에 추가되었습니다." : "찜 목록에서 삭제되었습니다";
        return ResponseEntity.ok(ApiResponse.success(message, isBookmarked));
    }

    // 내가 찜한 목록 조회
    // GET /api/bookmarks
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getMyBookmarks() {
        Long userNo = SecurityUtil.getCurrentUserNo();
        List<BookmarkResponse> list = bookmarkService.getMyBookmarks(userNo);
        return ResponseEntity.ok(ApiResponse.success("찜 목록 조회 성공", list));
    }
}
