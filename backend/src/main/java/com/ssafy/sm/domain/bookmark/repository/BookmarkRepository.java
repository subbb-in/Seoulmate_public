package com.ssafy.sm.domain.bookmark.repository;


import com.ssafy.sm.domain.bookmark.entity.Bookmark;
import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 특정 유저와 장소의 북마크 존재 여부 확인
    Optional<Bookmark> findByUserAndPlace(User user, Place place);

    // 특정 유저의 북마크 목록 조회 (장소 정보 fetch join 고려 가능)
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);

    // 삭제를 위한 메서드
    void deleteByUserAndPlace(User user, Place place);
}
