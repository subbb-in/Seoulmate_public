package com.ssafy.sm.domain.bookmark.entity;

import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_no")
    private Long bookmarkNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_no", nullable = false)
    private Place place;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 생성자 보조 메서드
    public static Bookmark create(User user, Place place) {
        return Bookmark.builder()
                .user(user)
                .place(place)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
