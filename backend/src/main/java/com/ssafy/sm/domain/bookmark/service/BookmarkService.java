package com.ssafy.sm.domain.bookmark.service;

import com.ssafy.sm.domain.bookmark.dto.BookmarkResponse;

import java.util.List;

public interface BookmarkService {
    //찜 토글(추가/삭제) -> 결과 상태 반환(true: 찜, false: 찜 취소)
    boolean toggleBookmark(Long userNo, Long placeNo);

    //내 찜 목록 조회
    List<BookmarkResponse> getMyBookmarks(Long userNo);
}
