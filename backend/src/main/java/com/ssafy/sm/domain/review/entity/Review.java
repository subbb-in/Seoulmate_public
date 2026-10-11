package com.ssafy.sm.domain.review.entity;

import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_no")
    private Long reviewNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_no")
    private Place place;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "hidden")
    private boolean hidden;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    // 비즈니스 로직: 리뷰 수정
    public void update(Integer rating, String content, boolean hidden) {
        if (rating != null) this.rating = rating;
        if (content != null) this.content = content;
        this.hidden = hidden;
    }

    // 비즈니스 로직: 리뷰 삭제 (Soft Delete)
    public void delete() {
        this.isDeleted = true;
    }

    // 연관 관계 편의 메서드
    public void addImage(ReviewImage image) {
        this.images.add(image);
        image.setReview(this);
    }
}
