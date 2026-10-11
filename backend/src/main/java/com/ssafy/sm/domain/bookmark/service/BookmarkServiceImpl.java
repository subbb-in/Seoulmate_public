package com.ssafy.sm.domain.bookmark.service;

import com.ssafy.sm.domain.bookmark.dto.BookmarkResponse;
import com.ssafy.sm.domain.bookmark.entity.Bookmark;
import com.ssafy.sm.domain.bookmark.repository.BookmarkRepository;
import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.place.repository.PlaceRepository;
import com.ssafy.sm.domain.user.entity.User;
import com.ssafy.sm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Override
    @Transactional
    public boolean toggleBookmark(Long userNo, Long placeNo) {
        User user = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Place place = placeRepository.findById(placeNo)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        // 이미 찜했는지 확인
        return bookmarkRepository.findByUserAndPlace(user, place)
                .map(bookmark -> {
                    // 이미 있으면 삭제
                    bookmarkRepository.delete(bookmark);
                    return false; // 결과 : 찜 해제
                })
                .orElseGet(() -> {
                    bookmarkRepository.save(Bookmark.create(user, place));
                    return true;
                });

    }

    @Override
    public List<BookmarkResponse> getMyBookmarks(Long userNo) {
        User user = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BookmarkResponse convertToResponse(Bookmark bookmark) {
        BookmarkResponse response = new BookmarkResponse();
        response.setBookmarkNo(bookmark.getBookmarkNo());
        response.setCreatedAt(bookmark.getCreatedAt());

        //장소 정보 매핑
        Place place = bookmark.getPlace();
        response.setPlaceNo(place.getPlaceNo());
        response.setTitle(place.getTitle());
        response.setAddr1(place.getAddr1());
        response.setFirstImage(place.getFirstImage());
        response.setRating(place.getRating());

        return response;
    }
}
