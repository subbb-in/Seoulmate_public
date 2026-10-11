package com.ssafy.sm.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_img_no")
    private Long reviewImgNo;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_no")
    private Review review;

    @Column(name = "review_img_name")
    private String reviewImgName;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;
}
