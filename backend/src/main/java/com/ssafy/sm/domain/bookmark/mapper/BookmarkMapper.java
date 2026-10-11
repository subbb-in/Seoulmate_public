package com.ssafy.sm.domain.bookmark.mapper;

import com.ssafy.sm.domain.bookmark.dto.BookmarkResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookmarkMapper {
    //찜 여부 확인(1이면 존재, 0이면 없음)
    int existsBookmark(@Param("userNo") Long userNo, @Param("placeNo") Long placeNo);

    //찜 추가
    void addBookmark(@Param("userNo") Long userNo, @Param("placeNo") Long placeNo);

    //찜 취소
    void removeBookmark(@Param("userNo") Long userNo, @Param("placeNo") Long placeNo);

    //내가 찜한 장소 목록 조회(Place Table과 Join)
    List<BookmarkResponse> findBookmarksByUser(Long userNo);
}
